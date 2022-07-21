const express = require('express');
const app = express();
const path = require('path');
const morgan = require('morgan');
const mongoose = require('mongoose');
const controller = require('./controller/controller');
const router = express.Router();
const {render} = require('ejs');


const dbURI = 'mongodb+srv://netninja:test1234@nodetutts.zewtt.mongodb.net/node-tuts?retryWrites=true&w=majority'

mongoose.connect(dbURI,{useNewUrlParser:true,useUnifiedTopology:true})
    .then((result)=>{
        //success
        console.log('success');
    })
    .catch((err)=>{
        console.log(err);
    })
app.set('views', path.join(__dirname, 'views'));
app.set('view engine','ejs');

app.use(express.static('public'));
app.use(express.urlencoded({extended:false}));
app.use(morgan('dev'));

app.get('/home',(req,res)=>{
    res.redirect('/');
})
router.get('/',controller.page_main);
router.get('/about',controller.page_about);
router.get('/login',controller.page_login);
router.get('/register',controller.page_register);
router.get('/share',controller.page_share);
router.post('/login', async(req,res)=>{
    controller.login_page_post(req,res);
} );
router.post('/register',async(req,res)=>{
    controller.register_page_post(req,res);
})
app.use('/',router);
app.listen(3000);

