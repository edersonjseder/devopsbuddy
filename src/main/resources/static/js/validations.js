/**
 * Created by root on 14/06/17.
 */
// Script that validates field by field in the contact form
$('#contactForm').validate({
    // Specifying the rules
    rules: {
        email: {
            required: true,
            email: true // Valid email
        },

        firstName: "required",
        lastName: "required",
        feedback: "required"
    },

    // Specifying the messages
    messages: {
        email: "Please enter a valid email address!",
        firstName: "First name field can't be blank!",
        lastName: "Last name field can't be blank!",
        feedback: "Feedback has to be filled!"
    },

    submitHandler: function (form) {
        form.submit();
    }

});

// Script that validates the fields of signup form
$('#signupForm').validate({
    // Specifying the rules
    rules: {
        email: {
            required: true,
            email: true // Valid email
        },
        username: "required",
        password: "required",
        confirmPassword: "required",
        firstName: "required",
        lastName: "required",
        phoneNumber: "required",
        country: "required",
    },

    //Specifying the messages
    messages: {
        email: "Please enter a valid email address!",
        username: "Username field can't be blank!",
        password: "Password field must be filled!",
        confirmPassword: "Confirm password field must be filled!",
        firstName: "First name field can't be blank!",
        lastName: "Last name field can't be blank!",
        phoneNumber: "Phone number must be filled",
        country: "Country is needed"
    },

    submitHandler: function (form) {
        form.submit();
    }
});