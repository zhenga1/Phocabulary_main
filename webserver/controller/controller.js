const bcrypt = require('bcrypt');
const Auth = require('../database/Auth');
const model = Auth.model;
const handleErrors = (err) =>{
    console.log(err.message,err.code);

}
const page_main = (req,res)=>{
    res.render('index',{title:"Phocabulary", slogan:"Learn with ease!"})
}
const page_about = (req,res)=>{
    res.render('about');
}
const page_login = (req,res)=>{
    console.log("successful call");
    res.render('login');
}
const page_register = (req,res)=>{
    console.log('calling register function');
    res.render('register')
}
const page_share = (req,res)=>{
    res.render('share');
}
const login_page_post = async function(req,res) {
    const {uname, pass} = req.body;

    try{
        const db = model.db();
        //check for username and password user
        // const existingUser = db.collections('auths').findOne({username:uname});
        // if(existingUser && Object.keys(existingUser).length()){
        //     Auth.hash()
        // }
        Auth.find({username:uname}).then((savedUser)=>{
            
        })

        res.status(201).json(loginUser);
    }catch(err){
        console.log(err);
        handleErrors(err);
        res.redirect('./login')
    }
}
const register_page_post = async function(req,res){
    const {runame, rpass,confrpass} = req.body;

    if(rpass!== confrpass)
    {
        res.status(400).json('password fields does not match');
    }
    try{
        //create a new user 
        const loginUser = await model.create({username:runame, password:rpass});
        res.status(201).json(loginUser);

    }catch(err){
        console.log(err);
        handleErrors(err);
        res.redirect('./login')
    }
    console.log(runame,rpass);
    ///res.send('user register');
}
module.exports={
    page_main,
    page_about,
    page_login,
    page_register,
    page_share,
    login_page_post,
    register_page_post
}
/*
const secretSalt = 254;
    var loginUser = new Auth(req.body);
    loginUser.findOne({ username: req.body.uname }, function(err, user) {
        if (err) throw err;
         
        // test a matching password
        user.comparePassword(req.body.pass, function(err, isMatch) {
            if (err) throw err;
            console.log('Password Matching ? ::', isMatch); // -&gt; Password123: true
        });
         
        // test a failing password
        user.comparePassword(req.body.pass, function(err, isMatch) {
            if (err) throw err;
            console.log('Password Matching ? ::'+ isMatch); // -&gt; 123Password: false
        });
    });
*/