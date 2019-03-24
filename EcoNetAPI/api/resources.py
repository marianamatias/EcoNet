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


class Resource(object):



    def on_get(self, req, resp):
        logging.info('Getting the resource')
        resource = {
            'id': 1,
            'name': 'Random Name'
        }
        resp.body = json.dumps(resource)
        resp.status = falcon.HTTP_200

    @falcon.before(api_key)
    @falcon.after(say_bye_after_operation)
    def on_post(self, req, resp):
        logging.info(req.get_param("data"))
        data = json.loads(req.get_param("data"))
        result = firebase.post('/tasks', data, {'print': 'pretty'}, {'X_FANCY_HEADER': 'VERY FANCY'})
        logging.info('Adding a task to firebase')
        resource = {
            'result': 'Recycle'
        }
        resp.body = json.dumps(resource)
        resp.status = falcon.HTTP_201

    @falcon.before(api_key)
    @falcon.after(say_bye_after_operation)
    def on_patch(self, req, resp):
        logging.info('Updating the resource')
        resource = {
            'id': 1,
            'name': 'Random Name'
        }
        resp.body = json.dumps(resource)
        resp.status = falcon.HTTP_200

    @falcon.before(api_key)
    @falcon.after(say_bye_after_operation)
    def on_delete(self, req, resp):
        logging.info('Deleting the resource')
        resource = {
            'id': 1,
            'name': 'Random Name'
        }
        resp.body = json.dumps(resource)
        resp.status = falcon.HTTP_200
