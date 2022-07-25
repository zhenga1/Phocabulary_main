const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const SALT_WORK_FACTOR = 254;
const bcrypt = require('bcrypt');

const AuthSchema = new Schema({
    username:{
        type:String,
        required:[true,'Please enter an email address!'],
        index:{unique:true},
        lowercase:true
    },
    password:{
        type:String,
        required:[true,'Please enter a password!'],
        //so basically the error string is run whenever there is an error
        minLength:[8,'minimum password length is 8 characters']
    }
});
const hashing = async() =>{
    const salt = await bcrypt.genSalt();
    this.password = await bcrypt.hash(user.password, salt);
};
//next is the next function existing in the middleware
AuthSchema.pre('save',async function(next){
    var user = this;
    const salt = await bcrypt.genSalt();
    this.password = await bcrypt.hash(user.password,salt);
    next();
    //if(user.isModified('password')) return next();

    // bcrypt.genSalt(SALT_WORK_FACTOR, function(err,salt){
    //     if(err) return next(err);

    //     bcrypt.hash(user.password,salt, function(err,hash){
    //         if(err) return next(err);

    //         user.password=hash;
    //         next();
    //     })
    // })
});

//fire a function after doc saved to db
AuthSchema.post('save', function (doc, next) {
    console.log('new user was created & saved', doc);
    next();
  });

AuthSchema.methods.comparePassword = function(candidatePassword, cb) {
    bcrypt.compare(candidatePassword, this.password, function(err, isMatch) {
        if (err) return cb(err);
        cb(null, isMatch);
    });
};

//lowercase is important
module.exports = {
    model: mongoose.model('auth', AuthSchema),
    hash: hashing
};