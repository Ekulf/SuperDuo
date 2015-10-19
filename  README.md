Football Scores API key
=======================

I include an api key for the Football Scores app include a file called private.properties in the FootballScores/app directory containing the following line: 

``FOOTBALL_API_KEY=my_api_key``

I added logging to gradle if the private.properties is not found or it does not properly contain the FOOTBALL_API_KEY gradle will log a warning message.