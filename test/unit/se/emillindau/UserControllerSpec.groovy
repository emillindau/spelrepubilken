package se.emillindau

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UserController)
@Mock(User)
class UserControllerSpec {

    void testPasswordsDoNotMatch() {
        request.method = 'POST'

        params.login = 'henry'
        params.password = 'password'
        params.confirm = 'wrongPassword'
        params.firstName= 'Henry'
        params.lastName = 'Rollins'

        def model = controller.register()
        def user = model.user

        assert user.hasErrors()
        assert 'user.password.dontmatch' == user.errors['password'].code
    }

    void testRegistrationFailed() {
        request.method = 'POST'

        params.login = ''

        def model = controller.register()
        def user = model.user

        assert user.hasErrors()
        assert session.user == null
        assert 'nullable' == user.errors['login'].code
        assert 'nullable' == user.errors['firstName'].code
        assert 'nullable' == user.errors['lastName'].code
    }

    void testRegistrationSuccess() {
        request.method = 'POST'

        params.login = 'henry'
        params.password = 'password'
        params.confirm = 'password'
        params.firstName = 'Henry'
        params.lastName = 'Rollins'

        controller.register()

        assert '/' == response.redirectedUrl
        assert session.user != null
    }

    void testLoginUserNotFound() {
        request.method = 'POST'

        params.login = 'frank'
        params.password = 'hotrats'

        controller.login()

        def cmd = model.loginCmd

        assert cmd.hasErrors()
        assert 'user.not.found' == cmd.errors['login'].code
        assert session.user == null
        assert '/home/index' == view
    }

    void testLoginFailurePasswordInvalid() {
        request.method = 'POST'

        def u = new User(login: 'maynard',
                firstName: 'Maynard',
                lastName: 'Keenan',
                password: 'undertow').save()
        assert u != null

        params.login = 'maynard'
        params.password = 'lateralus'

        controller.login()
        def cmd = model.loginCmd

        assert cmd.hasErrors()
        assert 'user.password.invalid' == cmd.errors['password'].code
        assert session.user == null
        assert '/home/index' == view
    }

    void testLoginSuccess() {
        request.method = 'POST'

        def u = new User(login: 'maynard',
                firstName: 'Maynard',
                lastName: 'Keenan',
                password: 'undertow').save()

        assert u != null

        params.login = 'maynard'
        params.password = 'undertow'

        controller.login()

        assert session.user != null
        assert '/' == response.redirectedUrl
    }

}
