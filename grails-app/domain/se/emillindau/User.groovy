package se.emillindau

class User {

    String login
    String password
    String firstName
    String lastName
    // Player player

    static constraints = {
        login blank: false, size: 5..15, matches: /[\S]+/, unique: true
        password blank: false, size: 5..15, matches: /[\S]+/
        firstName blank: false
        lastName blank: false
    }
}
