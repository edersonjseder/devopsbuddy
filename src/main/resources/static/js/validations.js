/**
 * Created by root on 14/06/17.
 */
// Script that validates field by field in the form
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
