# LotteryListFragmentActivity
Initial Release

The LotteryListFragmentActivity app  generates the California Lottery Super Lotto numbers.
The app retrieves the weekly lottery drawings using webscraping methods with the help of
Volley and Jsoup libraries and  and stores the values in the apps local database  and on the server.


		The apps consists of one activity using 3 fragments that assists the user in generating
		quickpick lottery numbers:

	    GeneratorFragment: Allows the user to input the min and max frequency ranges the lotto
                           and mega  numbers have been drawn, and from that pool of number, ten(10)
                           lottery quick picks are generated and displayed on the UI

	    PastLottoNumbers: Display the past lottery numbers, stored in the database

	    FrequencyFragment: Display the number of time each SuperLotto and Mega numbers have been
	                       drawn the past number of drawings.

   
      This app is a redesign of the SuperLottoPicker app, which implemented 3 separate activities in 
      contrast to this app one activity that switches between fragments. In addition, the previous apps 
      only stored the past 52 weeks of numbers scraped from the lottery website. All lottery data is now 
      stored on the server, which increases the total number of drawings greater than 52 week
