
$(document).ready(function(){
	"use strict";

	var window_width 	 = $(window).width(),
	window_height 		 = window.innerHeight,
	header_height 		 = $(".default-header").height(),
	header_height_static = $(".site-header.static").outerHeight(),
	fitscreen 			 = window_height - header_height;


	$(".fullscreen").css("height", window_height)
	$(".fitscreen").css("height", fitscreen);

  //-------- Active Sticky Js ----------//
  $(".default-header").sticky({topSpacing:0});


     if(document.getElementById("default-select")){
          $('select').niceSelect();
    };

    $('.img-pop-up').magnificPopup({
        type: 'image',
        gallery:{
        enabled:true
        }
    });

  // $('.navbar-nav>li>a').on('click', function(){
  //     $('.navbar-collapse').collapse('hide');
  // });


    //  Counter Js 

    $('.counter').counterUp({
        delay: 10,
        time: 1000
    });

    $('.play-btn').magnificPopup({
        type: 'iframe',
        mainClass: 'mfp-fade',
        removalDelay: 160,
        preloader: false,
        fixedContentPos: false
    });

    // Initialize carousels after DOM and scripts are ready
    $(window).on('load', function() {
        initializeCarousels();
    });
    
    // Fallback initialization after a short delay
    setTimeout(function() {
        initializeCarousels();
    }, 500);

    function initializeCarousels() {
        // Check if owlCarousel is loaded
        if (typeof $.fn.owlCarousel === 'undefined') {
            console.warn('owlCarousel not loaded yet, retrying...');
            setTimeout(initializeCarousels, 100);
            return;
        }

        // Initialize banner slider (hero section) FIRST - most important
        var bannerSlider = $('.active-blog-slider');
        if (bannerSlider.length && !bannerSlider.hasClass('owl-loaded')) {
            bannerSlider.owlCarousel({
                loop: true,
                dots: true,
                items: 1,
                autoplay: true,
                autoplayTimeout: 3000,
                autoplayHoverPause: true,
                smartSpeed: 600,
                animateOut: 'fadeOut',
                animateIn: 'fadeIn',
                nav: false,
                margin: 0,
                autoHeight: false,
                responsive: {
                    0: { items: 1 },
                    600: { items: 1 },
                    1000: { items: 1 }
                }
            });
        }

        // Initialize top destinations carousel
        var destCarousel = $('.active-works-carousel');
        if (destCarousel.length && !destCarousel.hasClass('owl-loaded')) {
            destCarousel.owlCarousel({
                items: 1,
                loop: true,
                margin: 30,
                dots: true,
                autoplay: true,
                autoplayTimeout: 4000,
                autoplayHoverPause: true,
                smartSpeed: 800,
                nav: true,
                navText: ['<span class="lnr lnr-arrow-left"></span>', '<span class="lnr lnr-arrow-right"></span>'],
                autoHeight: false,
                responsive: {
                    0: { items: 1, margin: 10 },
                    480: { items: 1, margin: 20 },
                    768: { items: 1, margin: 30 }
                }
            });
        }

        // Initialize gallery carousel
        var galleryCarousel = $('.active-gallery');
        if (galleryCarousel.length && !galleryCarousel.hasClass('owl-loaded')) {
            galleryCarousel.owlCarousel({
                items: 6,
                loop: true,
                dots: true,
                autoplay: true,
                autoplayTimeout: 3000,
                nav: true,
                navText: ["<span class='lnr lnr-arrow-up'></span>",
                    "<span class='lnr lnr-arrow-down'></span>"],
                margin: 0,
                responsive: {
                    0: { items: 2 },
                    480: { items: 3 },
                    768: { items: 4 },
                    900: { items: 6 }
                }
            });
        }
    }


    // Select all links with hashes
    $('.navbar-nav a[href*="#"]')
    // Remove links that don't actually link to anything
    .not('[href="#"]')
    .not('[href="#0"]')
    .on('click',function(event) {
    // On-page links
    if (
      location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') 
      && 
      location.hostname == this.hostname
    ) {
      // Figure out element to scroll to
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
      // Does a scroll target exist?
      if (target.length) {
        // Only prevent default if animation is actually gonna happen
        event.preventDefault();
        $('html, body').animate({
          scrollTop: target.offset().top-50
        }, 1000, function() {
          // Callback after animation
          // Must change focus!
          var $target = $(target);
          $target.focus();
          if ($target.is(":focus")) { // Checking if the target was focused
            return false;
          } else {
            $target.attr('tabindex','-1'); // Adding tabindex for elements not focusable
            $target.focus(); // Set focus again
          };
        });
      }
    }
    });

      $(document).ready(function() {
          $('#mc_embed_signup').find('form').ajaxChimp();
      });   

 });
