#-------------------------------------------------------------------------------
# Name:        version probe
# Purpose:     platform testing
#
# Author:      Matthias Mueller
#
# Created:     11.06.2012
# Copyright:   (c) Matthias Mueller 2012
# Licence:     <your licence>
#-------------------------------------------------------------------------------

import sys

try:
    print "python-%i.%i" % (sys.version_info[0], sys.version_info[1])
except:
    retval = None
