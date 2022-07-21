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
const login_page_post = (req,res)=>{
        
}
module.exports={
    page_main,
    page_about,
    page_login,
    page_register,
    page_share,
    login_page_post
}