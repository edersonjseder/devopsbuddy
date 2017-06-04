$( document ).ready( main );

function main() {
    $('.btn-collapse').click(function(e) {
       e.preventDefault(); //Avoid to reload the page
       var $this = $(this);
       var $collapse = $this.closest('.collapse-group').find('.collapse');
       $collapse.collapse('toggle');
    });
}