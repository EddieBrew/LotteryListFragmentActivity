# LotteryListFragmentActivity
Initial Release


    The LotteryListFragmentActivity app  generates the California Lottery Super Lotto numbers.
    The app retrieves the past 52 weeks drawings, using webscraping methods with the help of  
    Volley and Jsoup libraries and stores the values. An update will be provided for comments
    clarification and documentation


   The apps consists of one activity using 3 fragments that assists the user in generating
    quick pick lottery numbers:

    GeneratorFragment: Allows the user to input the min and max frequency ranges the lotto
		   and mega  numbers have been drawn, and from that pool of number, ten(10)
		   lottery ticket quick picts are generated and displayed on the UI

    PastLottoNumbers: Display the past 52 week drawn lottery numbers

    FrequencyFragment: Display the number of time each SuperLotto and Mega numbers have been
		       drawn the past 52 weeks.
                         
      This app is a redesign of the SuperLottoPicker app, which implemented 3 separate activities in 
      contrast to this app one activity that switches between fragments
