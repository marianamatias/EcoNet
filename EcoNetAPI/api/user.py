"""Resources Submodule"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import logging
import json
from firebase import firebase

from api.hooks import api_key, say_bye_after_operation

import falcon


#adding firebase connection/application
firebase = firebase.FirebaseApplication("https://fir-auth-93d22.firebaseio.com/", None)


class User(object):


#For now no need to deal with storing all the information from the user like the credentials and so on
	def on_get(self, req, resp):
		#The user is on the welcome activity and wants to retrieve all the database
		if req.get_param("param") == 'all':
			logging.info('Getting all the users')
			resource = firebase.get('/users',None)
			resp.body = json.dumps(resource)
			resp.status = falcon.HTTP_200
		#The user wants to retrieve only his profile on the database
		elif req.get_param("param") == 'myaccount':
			data = json.loads(req.get_param("data"))
			logging.info('Getting one user')
			resource2 = firebase.get('/users',data['ID'])
			resp.body = json.dumps(resource2)
			resp.status = falcon.HTTP_200


	@falcon.before(api_key)
	@falcon.after(say_bye_after_operation)
	def on_post(self, req, resp):
		data = json.loads(req.get_param("data"))
		if req.get_param("param") == 'new' :
			#We need to post the new users so add a new element
			logging.info('Adding a user to firebase')
			#The structure for data on post is :
			# data = {"firstname" : "Hadrien","lastname" : "RIVIERE","username" : "hadrrivi88","tasklist":{}, "challenges":{},"followedQuestion":{}}
			result = firebase.post('/users', data)
			#Retrieve the 'name' of the user which is his ID on to the FRONT-END
		elif req.get_param('param') == 'challenge' : 
			#An user wants to challenge another one
			#We need to post on both account the new challenge
			logging.info("Adding a challenge in both challengers")
			result = firebase.post('/users/'+data['fromUserID']+'/challenge',data['challengeFrom'])
			result = firebase.post('/users/'+data['otherUserID']+'/challenge',data['challengeTo'])
		resp.body = json.dumps(result)
		resp.status = falcon.HTTP_201

	@falcon.before(api_key)
	@falcon.after(say_bye_after_operation)
	def on_patch(self, req, resp):
	#We need to be able to change the challenges, followedQuestion and the tasklist 
	#All of this is quite able to change quickly so we'll edit all the user at once for this
		data = json.loads(req.get_param("data"))
		if req.get_param('param') == 'challenge_status':
			logging.info('Updating the challenge status for both challengers')
			result = firebase.patch('/users/'+data['fromUserID']+'/challenge',data['challengeFrom'])
			result = firebase.patch('/users/'+data['otherUserID']+'/challenge',data['challengeTo'])
		elif req.get_param('param') == 'update':
		#Only updating the tasklist, challenge and followedQuestion periodically
			logging.info('Updating the user')
			result = firebase.patch('/users/'+data['userID']+'/tasklist',data['tasklist'])
			result = firebase.patch('/users/'+data['userID']+'/followedQuestion',data['followedQuestion'])
			result = firebase.patch('/users/'+data['userID']+'/challenge',data['challenge'])
		resource = 'updated'
		resp.body = json.dumps(resource)
		resp.status = falcon.HTTP_200

	@falcon.before(api_key)
	@falcon.after(say_bye_after_operation)
	def on_delete(self, req, resp):
		#One of the challengers has declined, or deleted the challenge
		#need to remove this challenge for both challengers
		#The challenge ID is the otherchallengerID which explain the path to delete
		logging.info('Deleting a challenge for User 1 and User 2')
		data = json.loads(req.get_param("data"))
		result = firebase.delete('/users/'+data['otherUserID']+'/challenge/',data['fromUserID'])
		result = firebase.delete('/users/'+data['fromUserID']+'/challenge/',data['otherUserID'])
		resource = 'deleted'
		resp.body = json.dumps(resource)
		resp.status = falcon.HTTP_200
