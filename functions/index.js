const functions = require('firebase-functions');

const request = require('request-promise')

exports.indexPostsToElastic = functions.database.ref('/users_profile/{user_id}')
	.onWrite(event => {
		let postData = event.data.val();
		let user_id = event.params.user_id;
		
		console.log('Indexing users:', postData);
		
		let elasticSearchConfig = functions.config().elasticsearch;
		var elasticSearchBaseUrl = 'http://35.188.122.241//elasticsearch/';
		let elasticSearchUrl = elasticSearchBaseUrl + 'users_profile/users/' + user_id;
		let elasticSearchMethod = postData ? 'POST' : 'DELETE';
		
		return request({
			method: elasticSearchMethod,
			url: elasticSearchUrl,
			auth:{
				username: elasticSearchConfig.username,
				password: elasticSearchConfig.password,
			},
			body: postData,
			json: true
		  });
		  
		  
	});

