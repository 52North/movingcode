#!/usr/bin/python

import fileinput, os
import sys, getopt
import shutil
import uuid
import ConfigParser
from contextlib import closing
from zipfile import ZipFile, ZIP_DEFLATED

TEMPLATE_FILE = "template_definition.xml"
WORKSPACE_SUB_FOLDER = "workspace"

def copyAndPrepareTemplateDefinition(cwd, arguments, dataset_base):
	print dataset_base
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
		
def removeResources(tempDef):
	shutil.rmtree(WORKSPACE_SUB_FOLDER)
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
	print cfgpath
	config.read(cfgpath)
	
	#prepare the template file
	tempDefinition = copyAndPrepareTemplateDefinition(cwd, sys.argv, config.get("workspace", "dataset_base_folder"))
	
	print "Using temporary definition file: "+ tempDefinition
	os.system("gtcontrol "+tempDefinition)
	
	#finally, copy output files
	copyResultToOutputFiles(sys.argv)
	
	#cleanup after us
	#removeResources(tempDefinition)

if __name__ == "__main__":
	main(sys.argv[1:])
