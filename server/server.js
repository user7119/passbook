console.log('May Node be with you')
const express = require('express')
const app = express()
const bodyParser = require('body-parser')
const MongoClient = require('mongodb').MongoClient

MongoClient.connect('mongodb+srv://ypd123:ypd123@cluster0-ivpmb.mongodb.net/admin?retryWrites=true&w=majority',(err,client)=>{
	if (err) return console.log(err)
	db = client.db('star-wars-quotes')

	app.listen(3000,()=>{
		console.log('listening on 3000')
	})
})

app.use(bodyParser.urlencoded({extended:true}))

app.get('/',(req,res) => {
	res.send('Hello World')
	// res.sendFile(__dirname + '/index.html')
})

app.post('/quotes',(req,res)=>{
	db.collection('quotes').save(req.body,(err,result)=>{
		if(err) {
	
			return console.log(err)
		}
		
		console.log('saved to database')

	})
	res.send('db error')
})

app.get('/downloadApk',function(req, res){
	var file = __dirname + '/passbook.apk'
	res.download(file)
})
