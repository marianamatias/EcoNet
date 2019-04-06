"""API MODULE"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from api.middleware import AuthMiddleware
#from api.resources import Resource
from api.task import Task
from api.user import User
#from api.advice import Advice

import falcon


def generic_error_handler(ex, req, resp, params):

	if isinstance(ex, falcon.HTTPNotFound):
		raise falcon.HTTPNotFound(description='Not Found')
	elif isinstance(ex, falcon.HTTPMethodNotAllowed):
		raise falcon.HTTPMethodNotAllowed(falcon.HTTP_405, description='Method Not Allowed')
	else:
		raise


app = falcon.API(
#	  middleware=[
#	  AuthMiddleware()
# ]
)


#app.add_route('/example', Resource())
app.add_route('/task',Task())
app.add_route('/user',User())
#app.add_route('/advice",Advice())
app.add_error_handler(Exception, generic_error_handler)
