#!/usr/bin/python

import fileinput, os
import sys, getopt
import shutil
import uuid
import ConfigParser
from contextlib import closing
from zipfile import ZipFile, ZIP_DEFLATED
from subprocess import call, Popen, PIPE
import getpass
import tempfile

TEMPLATE_FILE = "template_definition.xml"
WORKSPACE_SUB_FOLDER = "workspace"

def copyAndPrepareTemplateDefinition(cwd, arguments, dataset_base):
	print "Data folder: "+dataset_base
	tmpDef = uuid.uuid4().hex +".xml"
	
	datasetAFolder = dataset_base + os.sep + arguments[1]
	datasetBFolder = dataset_base + os.sep + arguments[2]
	
	index = arguments[1].find('/')
	if (index < 0):
		arguments[1].find('\\')
	datasetAName = arguments[1][:index]
	
	index = arguments[2].find('/')
	if (index < 0):
		arguments[2].find('\\')
	datasetBName = arguments[2][:index]
	
	shutil.copyfile(TEMPLATE_FILE, tmpDef)
	for line in fileinput.input(tmpDef, inplace = 1): # Does a list of files, and writes redirects STDOUT to the file in question
		newLine = line.replace("${workspace}", cwd + os.sep + WORKSPACE_SUB_FOLDER)
		newLine = newLine.replace("${dataset_a_base_folder}", datasetAFolder)
		newLine = newLine.replace("${dataset_b_base_folder}", datasetBFolder)
		newLine = newLine.replace("${satellite_a}", datasetAName)
		newLine = newLine.replace("${satellite_b}", datasetBName)
		newLine = newLine.replace("${CollocationCriteria_dt}", arguments[3])
		newLine = newLine.replace("${CollocationCriteria_dp}", arguments[4])
		newLine = newLine.replace("${ResamplingScheme}", arguments[5])
		newLine = newLine.replace("${ResamplingMaster}", arguments[6])
		print newLine,
		
	return tmpDef

def zipdir(basedir, archivename):
    assert os.path.isdir(basedir)
    with closing(ZipFile(archivename, "w", ZIP_DEFLATED)) as z:
        for root, dirs, files in os.walk(basedir):
            #NOTE: ignore empty directories
            for fn in files:
                absfn = os.path.join(root, fn)
                zfn = absfn[len(basedir)+len(os.sep):] #XXX: relative path
                z.write(absfn, zfn)

def copyResultToOutputFiles(arguments):
	dest = arguments[7]
	shutil.copy(WORKSPACE_SUB_FOLDER + os.sep + "output"+ os.sep +"report.pdf", dest)
	
	if (len(arguments) > 8):
		dest = arguments[8]
		zipdir(WORKSPACE_SUB_FOLDER + os.sep + "output", dest)
		
def removeResources(tempDef, temp_mpl_dir):
	shutil.rmtree(WORKSPACE_SUB_FOLDER)
	shutil.rmtree(temp_mpl_dir)
	os.remove(tempDef)

def main(argv):	

	if (len(sys.argv) < 8):
		print "Usage: python mc-geca.py <dataset_a> <dataset_b> <CollocationCriteria_dt> <CollocationCriteria_dp> <ResamplingScheme> <ResamplingMaster> <pdfOutputFile> [<zipOutputFile>]"
		sys.exit()

	#change the working dir to the scripts folder
	cwd = os.path.dirname(os.path.realpath(__file__))
	os.chdir(cwd)
	print "Working dir: " +os.getcwd()

	#read config
	config = ConfigParser.ConfigParser()
	cfgpath = "geca-config.cfg"
	config.read(cfgpath)
	
	#prepare the template file
	tempDefinition = copyAndPrepareTemplateDefinition(cwd, sys.argv, config.get("workspace", "dataset_base_folder"))

	#add /usr/local/bin to path - the default location of gtcontrol and the like
	path = os.getenv("PATH")
	os.environ['PATH'] = "/usr/local/bin" + os.pathsep + path
	print "$PATH set to: " +os.getenv("PATH")
	
	pWhich = Popen(["which", "gtcontrol"], stdout=PIPE)
	gtControlFull, err = pWhich.communicate()
	gtControlFull = gtControlFull.strip()
	
	if not gtControlFull.strip():
		gtControlFull = "gtcontrol"
	
	print "Found gtcontrol at: "+ gtControlFull
	
	#we need to set MPLCONFIGDIR
	temp_mpl_dir = tempfile.mkdtemp()
	os.environ['MPLCONFIGDIR'] = temp_mpl_dir
	
	print "Using temporary definition file: "+ tempDefinition
	#os.system("gtcontrol "+tempDefinition)
	print "calling '" +gtControlFull+ " " +cwd + os.sep + tempDefinition +"'"
	call([gtControlFull, cwd + os.sep + tempDefinition])
	
	#finally, copy output files
	copyResultToOutputFiles(sys.argv)
	
	#cleanup after us
	removeResources(tempDefinition, temp_mpl_dir)

if __name__ == "__main__":
	main(sys.argv[1:])
