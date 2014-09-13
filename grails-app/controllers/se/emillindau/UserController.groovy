package se.emillindau

class UserController {

    def register() {
        if(request.method == 'POST') {
            def u = new User()
            u.properties['login', 'password', 'firstName', 'lastName'] = params

            // if pw and confirm aren't equal
            if(u.password != params.confirm) {
                u.errors.rejectValue("password", "user.password.dontmatch")
                return [user: u]
            } else if(u.save()) {
                session.user = u
                redirect(controller: "home")
            } else {
                return [user: u]
            }
        }
    }

    def login(LoginCommand cmd) {
        if(request.method == 'POST') {
            if(!cmd.hasErrors()) {
                session.user = cmd.getUser()
                redirect(controller: 'home')
            } else {
                render view:'/home/index', model: [loginCmd: cmd]
            }
        } else {
            render view: '/home/index'
        }
    }
}

class LoginCommand {

    String login
    String password
    private User u

    User getUser() {
        if(!u && login) {
            u = User.findByLogin(login)
        }
        return u
    }

    static constraints = {
       login blank: false, validator: { val, obj ->
           if(!obj.user)
               return "user.not.found"
       }
        password blank: false, validator: {val, obj ->
            if(obj.user && obj.user.password != val)
                return "user.password.invalid"
        }
    }

}
