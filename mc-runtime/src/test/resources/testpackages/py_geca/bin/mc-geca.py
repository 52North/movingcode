#!/usr/bin/python

import fileinput, os
import sys, getopt
import shutil
import uuid

TEMPLATE_FILE = "template_definition.xml"
WORKSPACE_SUB_FOLDER = "workspace"

def copyAndPrepareTemplateDefinition(cwd, arguments):
	tmpDef = uuid.uuid4().hex +".xml"
	
	shutil.copyfile(TEMPLATE_FILE, tmpDef)
	for line in fileinput.input(tmpDef, inplace = 1): # Does a list of files, and writes redirects STDOUT to the file in question
		newLine = line.replace("${workspace}", cwd + os.sep + WORKSPACE_SUB_FOLDER)
		newLine = newLine.replace("${satellite_a}", arguments[1])
		newLine = newLine.replace("${satellite_b}", arguments[2])
		newLine = newLine.replace("${CollocationCriteria_dt}", arguments[3])
		newLine = newLine.replace("${CollocationCriteria_dp}", arguments[4])
		newLine = newLine.replace("${ResamplingScheme}", arguments[5])
		newLine = newLine.replace("${ResamplingMaster}", arguments[6])
		print newLine,
		
	return tmpDef

def copyResultToOutputFile(pdfDest):
	shutil.copy(WORKSPACE_SUB_FOLDER + os.sep + "output/report.pdf", pdfDest)

def main(argv):	

	if (len(sys.argv) < 8):
		print "Usage: python mc-geca.py <satellite_a> <satellite_b> <CollocationCriteria_dt> <CollocationCriteria_dp> <ResamplingScheme> <ResamplingMaster> <pdfOutputFile>"
		sys.exit()

	for arg in sys.argv:
		print arg
	
	cwd = os.path.dirname(os.path.realpath(__file__))
	os.chdir(cwd)
	print os.getcwd()
	outputPdfFile = sys.argv[7]
	
	tempDefinition = copyAndPrepareTemplateDefinition(cwd, sys.argv)
	
	print "Using temporary definition file: "+ tempDefinition
	os.system("gtcontrol "+tempDefinition)
	
	copyResultToOutputFile(outputPdfFile)

if __name__ == "__main__":
	main(sys.argv[1:])


