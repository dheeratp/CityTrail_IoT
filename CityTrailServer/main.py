#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
from google.appengine.ext import ndb
import json
import ast
import datetime
import random
import urllib2


request_payload ={
 "actor": {
   "displayName": "Name",
   "objectType": "person"
 },
 "verb": "request",
 "object": {
   "objectType": "place",
   "id": "http://example.org/berkeley/southhall/meetingroom",
   "displayName": "Meeting room 206 at South Hall, UC Berkeley",
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
 "startTime": "2015-01-06T15:04:55.000Z",
 "endTime": "2015-01-06T15:04:55.000Z",
 "provider": {
   "displayName": "BerkeleyRoom"
 }
}


class Activity(ndb.Model):
    content = ndb.StringProperty()
    date = ndb.StringProperty()

    @classmethod
    def query_today(cls):
        return cls.query(cls.date == str(datetime.date.today()))


class MainHandler(webapp2.RequestHandler):

    def get(self):
        posts = Activity.query()
        for post in posts:
        	self.response.out.write(post.content) 


    def post(self):
    	data = self.request.body
        date = str(datetime.date.today())
    	activity = Activity(content=data, date=date)
    	activity.put()

class CronHandler(webapp2.RequestHandler):


    def get(self):
        today = Activity.query_today()
        d = {}
        for e in today:
            dict_obj = ast.literal_eval(e.content)
            name = dict_obj['actor']['displayName']
            if name not in d.keys():
                d[name] = 0
            d[name] += int(dict_obj['object']['content'])
        winner = max(d)
        start_time = datetime.datetime.now()
        end_time = start_time + datetime.timedelta(minutes=10)
        start_str = start_time.isoformat()
        end_str = end_time.isoformat()

        request_payload['actor']['displayName'] = winner
        request_payload['startTime'] = start_str
        request_payload['endTime'] = end_str

        headers =  {'Content-Type': 'application/stream+json'}
        request = urllib2.Request("http://russet.ischool.berkeley.edu:8080/activities", data=json.dumps(request_payload), headers=headers)
        response = urllib2.urlopen(request)

    
    

app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/task', CronHandler)
], debug=True)



# to update app: 
# appcfg.py update stepcountercontest/