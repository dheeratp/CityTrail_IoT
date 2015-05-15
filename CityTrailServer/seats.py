import json, urllib, urllib2
import datetime
import requests


payload= {
 "actor": {
   "displayName": "Student Name",
   "objectType": "person"
 },
 "verb": "request",
 "object": {
   "objectType": "place",
   "id": "http://example.org/berkeley/southhall/202/chair/1"
   "displayName": "Chair at 202 South Hall, UC Berkeley",
   "position": {
     "latitude": 34.34,
     "longitude": -127.23,
     "altitude": 100.05
   },
   "address": {
     "locality": "Berkeley",
     "region": "CA",
   },
   "descriptor-tags": [
     "chair",
     "rolling"
   ]
 },

}

headers =  { 'Content-Type': 'application/stream+json' }
r = requests.post("http://russet.ischool.berkeley.edu:8080/activities", data=json.dumps(payload), headers=headers)
print r.text