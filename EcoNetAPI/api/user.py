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
			if req.get_param("wanted") == 'all':
				data = json.loads(req.get_param("data"))
				logging.info('Getting one user')
				resource2 = firebase.get('/users',data['ID'])
				resp.body = json.dumps(resource2)
				resp.status = falcon.HTTP_200
			elif req.get_param("wanted") == 'tasklist':
				data = json.loads(req.get_param("data"))
				logging.info('Getting one tasklist user')
				resource2 = firebase.get('/users/'+data['ID']+'/tasklist',None)
				resp.body = json.dumps(resource2)
				resp.status = falcon.HTTP_200
			elif req.get_param("wanted") == 'challenge':
				data = json.loads(req.get_param("data"))
				logging.info('Getting one user')
				resource2 = firebase.get('/users/'+data['ID']+'/challenge',None)
				resp.body = json.dumps(resource2)
				resp.status = falcon.HTTP_200
			elif req.get_param("wanted") == 'followedQuestion':
				data = json.loads(req.get_param("data"))
				logging.info('Getting one followedQuestion user')
				resource2 = firebase.get('/users/'+data['ID']+'/followedQuestion',None)
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
			result1 = firebase.post('/users', data)
			#Retrieve the 'name' of the user which is his ID on to the FRONT-END
			resp.body = json.dumps(result1)
			resp.status = falcon.HTTP_201
		elif req.get_param('param') == 'challenge' : 
			#An user wants to challenge another one
			#We need to post on both account the new challenge
			logging.info("Adding a challenge in both challengers")
			challengeForME = json.loads(req.get_param("myChallenge"))
			challengeForYou = json.loads(req.get_param("otherChallenge"))
			result21 = firebase.post('/users/'+req.get_param('UserID')+'/challenge',challengeForME)
			result22 = firebase.post('/users/'+req.get_param('otherUserID')+'/challenge',challengeForYou)
			resp.body = json.dumps(result22)
			resp.status = falcon.HTTP_201

	@falcon.before(api_key)
	@falcon.after(say_bye_after_operation)
	def on_patch(self, req, resp):
	#We need to be able to change the challenges, followedQuestion and the tasklist 
	#All of this is quite able to change quickly so we'll edit all the user at once for this
		if req.get_param('param') == 'challenge_status':
			logging.info('Updating the challenge status for both challengers')
			challengeForME = json.loads(req.get_param("myChallenge"))
			challengeForYou = json.loads(req.get_param("otherChallenge"))
			result = firebase.patch('/users/'+req.get_param('UserID')+'/challenge',challengeForME)
			result = firebase.patch('/users/'+req.get_param('otherUserID')+'/challenge',challengeForYou)
			resource2 = 'updated'
			resp.body = json.dumps(resource2)
			resp.status = falcon.HTTP_200
		elif req.get_param('param') == 'update':
			if req.get_param('wanted') == 'all':
		#Only updating the tasklist, challenge and followedQuestion periodically
				tasklist = json.loads(req.get_param("tasklist"))
				challenge = json.loads(req.get_param("challenge"))
				followedQuestion = json.loads(req.get_param("followedQuestion"))
				logging.info('Updating the user')
				result = firebase.patch('/users/'+req.get_param('userID')+'/tasklist',tasklist)
				result = firebase.patch('/users/'+req.get_param('userID')+'/followedQuestion',challenge)
				result = firebase.patch('/users/'+req.get_param('userID')+'/challenge',followedQuestion)
				resource = 'updated'
				resp.body = json.dumps(resource)
				resp.status = falcon.HTTP_200
			elif req.get_param('wanted') == 'tasklist':	
				tasklist = json.loads(req.get_param("tasklist"))
				logging.info('Updating the user')
				result = firebase.patch('/users/'+req.get_param('userID')+'/tasklist',tasklist)
				resource = 'updated'
				resp.body = json.dumps(resource)
				resp.status = falcon.HTTP_200
			elif req.get_param('wanted') == 'challenge':	
				challenge = json.loads(req.get_param("challenge"))
				logging.info('Updating the user')
				result = firebase.patch('/users/'+req.get_param('userID')+'/challenge',challenge)
				resource = 'updated'
				resp.body = json.dumps(resource)
				resp.status = falcon.HTTP_200
			elif req.get_param('wanted') == 'followedQuestion':	
				followedQuestion = json.loads(req.get_param("followedQuestion"))
				logging.info('Updating the user')
				result = firebase.patch('/users/'+req.get_param('userID')+'/followedQuestion',followedQuestion)
				resource = 'updated'
				resp.body = json.dumps(resource)
				resp.status = falcon.HTTP_200

	@falcon.before(api_key)
	@falcon.after(say_bye_after_operation)
	def on_delete(self, req, resp):
		#One of the challengers has declined, or deleted the challenge
		#need to remove this challenge for both challengers
		#The challenge ID is the otherchallengerID which explain the path to delete
		if req.get_param('param')== 'task':
			logging.info('Deleting one task')
			result = firebase.delete('/users/'+req.get_param('userID')+'/tasklist',req.get_param('taskID'))
			resource = 'deleted'
			resp.body = json.dumps(resource)
			resp.status = falcon.HTTP_200
		elif req.get_param('param') == 'challenge':
			logging.info('Deleting a challenge for User 1 and User 2')
			result = firebase.delete('/users/'+req.get_param('otherUserID')+'/challenge/',req.get_param('userID'))
			result = firebase.delete('/users/'+req.get_param('userID')+'/challenge/',req.get_param('otherUserID'))
			resource2 = 'deleted'
			resp.body = json.dumps(resource2)
			resp.status = falcon.HTTP_200
		elif req.get_param('param') == 'question':
			logging.info('Deleting a question from the followed list')
			result = firebase.delete('/users/'+req.get_param('userID')+'/followedQuestion/',req.get_param('questionID'))
			resource3 = 'deleted'
			resp.body = json.dumps(resource3)
			resp.status = falcon.HTTP_200
		
		
		
		
