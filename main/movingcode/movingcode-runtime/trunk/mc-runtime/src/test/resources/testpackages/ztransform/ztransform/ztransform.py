#!/usr/bin/env python
#****************************************************************************************
# 
#  Project:  OWS-8
#  Purpose:  Calculate z-Transformation from single band rasters
#  Author:   Matthias Mueller, matthias_mueller@tu-dresden.de
#  Web:      www: http://tu-dresden.de/fgh/geo/gis
# 
#****************************************************************************************

try:
    from osgeo import gdal
    from osgeo.gdalnumeric import *
except ImportError:
    import gdal
    from gdalnumeric import *

import sys, os
import numpy as np


FORMAT = 'GTiff'
GDAL_OPTS = ['TILED=YES','COMPRESS=LZW']

# computes zTransform, uses chunking to save memory
# uses valsDSList to compute MEAN and STDDEV
# uses xBD as X for the z-transformation
# uses outBD to write the output
# all DS must(!) have the same XSize and YSize
def zTransform(valDSList, xBD, outBD):
	valBDList = []
	
    # fetch the bands
	for valDS in valDSList:
		valBDList.append(valDS.GetRasterBand(1))
	
	# determine block size
	XSize = valDSList[0].RasterXSize
	YSize = valDSList[0].RasterYSize
	myBlockSize = valBDList[0].GetBlockSize()
	# store these numbers in variables that may change later
	nXValid = myBlockSize[0]
	nYValid = myBlockSize[1]
	# find total x and y blocks to be read
	nXBlocks = (int)((XSize + myBlockSize[0] - 1) / myBlockSize[0])
	nYBlocks = (int)((YSize + myBlockSize[1] - 1) / myBlockSize[1])
	myBufSize = myBlockSize[0] * myBlockSize[1]
	
	# start looping through blocks of data
	# loop through X-lines
	for X in range(0, nXBlocks):
		
		# in the rare (impossible?) case that the blocks don't fit perfectly
		# change the block size of the final piece
		if X == nXBlocks-1:
			nXValid = XSize - X * myBlockSize[0]
			myBufSize = nXValid * nYValid
		
		# find X offset
		myX = X * myBlockSize[0]
		
		# reset buffer size for start of Y loop
		nYValid = myBlockSize[1]
		myBufSize = nXValid * nYValid
		
		# loop through Y lines
		for Y in range(0, nYBlocks):
			
			# change the block size of the final piece
			if Y == nYBlocks-1:
				nYValid = YSize - Y * myBlockSize[1]
				myBufSize = nXValid * nYValid
				
			# find Y offset
			myY = Y * myBlockSize[1]
			
			# calculate zTransform
			valueArrayList = []
			for valBD in valBDList:
				valueArrayList.append(valBD.ReadAsArray(xoff=myX, yoff=myY, win_xsize=nXValid, win_ysize=nYValid))
			
			xArray = xBD.ReadAsArray(xoff=myX, yoff=myY, win_xsize=nXValid, win_ysize=nYValid)
			outArray = calcZTrans(valueArrayList, xArray)
			
			# write data block to the output file
			outBD.WriteArray(outArray, xoff=myX, yoff=myY)
			valueArrayList = None
			xArray = None
			outArray = None
			
	valBDList = None
	
	return

def calcZTrans(valArrayList, xArray):
	
	# stack all arrays
	array3D = numpy.dstack(valArrayList)
	# print array3D.shape
	
	# create new arrays for MEAN and STDDEV
	meanArray = array3D.mean(2)
	stdArray = array3D.std(2)
	
	# z-Transformation
	outArray = (xArray - meanArray)/stdArray
	
	return outArray	

def main(*args):
	try:
		outFile = args[1]
		xFile = args[2]
		statsFiles = args[3:len(args)]
		# print(outFile, xFile, statsFiles)
		
		# open statsFiles
		statsDSList = []
		for file in statsFiles:
			ds = gdal.Open(file)
			statsDSList.append(ds)
		
		# open xFile
		xDS = gdal.Open(xFile)
		xBD = xDS.GetRasterBand(1)
		
		# create Output
		XSize = xDS.RasterXSize
		YSize = xDS.RasterYSize
		geotransform = xDS.GetGeoTransform()
		projection = xDS.GetProjection()
		drv = gdal.GetDriverByName ( FORMAT )
		outDS = drv.Create( outFile, XSize, YSize,1,gdal.GDT_Float32,options=GDAL_OPTS )
		outBD = outDS.GetRasterBand(1)
		
		zTransform(statsDSList, xBD, outBD)
		
		# clean up stuff
		for item in statsDSList:
			item = None
		
		xBD = None
		xDS = None
		outBD = None
		outDS = None
		
	except:
		return 1
	else:
		return 0 # exit errorlessly
 
if __name__ == '__main__':
    sys.exit(main(*sys.argv))

