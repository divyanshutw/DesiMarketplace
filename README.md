![logowithtext](https://github.com/LalitKumarTyagi/Desi-Marketplace/blob/master/logowithtext.png)
# Desi-Marketplace

### **Problems**

* 1. At the time of lockdown when most established brands were struggling with their supply chain, the young blood stepped up its game to make its presence felt and let the world know that they care and can deliver. These local products lost their significance with the emergence of e-shopping. Now is the time to bring them online so that they could regain their value.

* 2. The lockdown broke the supply chain and Desi Marketplace will strengthen the supply chain so that no lockdown can bring poverty to our manufacturers and retailers.
 
## **How is Desi Marketplace different from other B2B applications?(Solution to problem i)**

To know the answer to this question, one must understand that **bringing these local Manufacturers and Retailers in an online platform is not a straightforwar idea**. Their marketing strategy is different. 
* 1. Buyers have a practice of bargaining while interacting with them. 
* 2. Most local retailers and manufacturers maintain book-keeping and of course other B2B applications support either online payment(prepaid) or Pay on Delivery.
* 3. How can you bring a General Store online? A general store has hundreds of products to satisfy consumer demands.
* 4. Without talking (through chat or call), how can the buyer and seller interact?

So, Desi Marketplace is a larger scale version of a typical B2B application where these types of Manufacturers and Retailers who can't be brought into the mainstream online shopping are also targeted. Wondering how we do it, then just go through the features of the app.

## **How Desi marketplace connects the supply chain?(Solution to problem ii)**

Most **B2B applications and websites are simply buyer and seller based**. Well the supply chain is not that simple. A typical supply chain does not have just **a** buyer and **a** seller, it comprises of < `Raw material manufacturer-> Product manufacturer-> Wholesaler-> Retailer-> Consumer` >. **E-shopping sites like Amazon, Flipkart, Myntra, etc cut the supply chain and connect the Manufacturer directly to the Consumers**. Now you know how are they able to give such huge discounts by keeping the middle men aside. Well this might sound good to you but the fact is that these **small retailers contribute to about 10% of India's GDP** ([Reference](https://www.ibef.org/industry/retail-india.aspx)).
![Capture](https://github.com/divyanshutw/DesiMarketplace/blob/master/Images/Capture.PNG)

After analysing different supply chains we proposed a completely new concept. The idea is to **divide all the components of the supply chain into Manufacturers, Retailers and Consumers**. This way the whole system will work as it is but online.

---

## Features of the app:

### Search system:
We have designed a **versatile search system** for all types of Manufacturers, Retailers and Consumers and for all scales of supply chain from large to small. 
The search system has two filters namely "Digital payment available", "Home delivery available". Also the users can sort the serach results on the basis of "Rating" and "Distance between their location and the seller's location".

### Authentication: 
All the users must create an account using email and password and register themselves either as Manufacturer, Retailer or Consumer and fill the required information.

### Favourites:
Who wants to search for the same dairy everyday to buy milk? So we will include a favourite list to better the user experience.

### Rating: 
The rating system will be included so that a user can prioritize from the list of shops available in the search results.

### Maps: 
Every user must select his location on map while registering. This way not only can a user find the closest enterprise but also he could see where exactly the enterprise is located on the map. For this we have used the **Google Maps API.**

### Call: 
The users can call each other to interact and solve complex issues.

### In-app messaging: 
The app will have a realtime in-app messaging feature where the the dealers can chat and finalise their deals.

### Book-keeping: 
As stated earlier, enterprises like dairy shops, general stores, etc. prefer to keep a book with their daily customers. The app will have a realtime feature for it too.

### Orders History: 
The app will have a feature which maintains the order history and the users could review the orders in future if needed. This could also be used as an invoice or an order summary.

### News: 
Desi Marketplace and our Government, both are striving to uplift the MSME sector. So we decided to work for the common purpose and have included a news feature for Manufacturers and Retailers. After the revised definition of MSME, more enterprises are included in the Act and can avail the facilities offered by the Government. Since most local manufacturers and retailers are unaware of these facts, this feature will help a lot.

### Ads: 
The Manufacturers and Retailers can advertise their enterprise or products. For this they will have to send a poster and a reach limit and all the users in that limit will be able to see the ad while scrolling their searches. For example, **if the reach limit is 2km, all the users in the radius of 2km will be able to see the ads.**

---

## Tools used:
Java, Android Studio, Google Maps API, Firebase

## Advantages of using firebase's firestore as a database:
* 1. Fast
* 2. Data is stored as **collections of documents** and not like other databases where data is stored in form of branches. So if one branch is extracted, all its children are also extracted. But in firestore only a single document is extracted which **reduces the user's data usage.**
* 3. Reads and writes limit and pricing is good.
* 4. All the reads and writes have to go through security checks. We have set the **security rules** to secure the privacy of the authenticated users.

---

## Workflow of activities:
1. Login page.
- ![WhatsApp%20Image%202020-07-23%20at%2019.27.59%20(1)](https://github.com/divyanshutw/DesiMarketplace/blob/master/Images/WhatsApp%20Image%202020-07-23%20at%2019.27.59%20(1).jpeg)
   - 1.1 If the user has already signed up, he can simply login or can go to the signup page.
   - 1.2 Three different types of activities based on whether the user is a Manufacturer, Retailer or a Consumer.
   - 1.3 After signup or login, the user is directed to the home page.

2. The home page is displayed as follows:
   - ![WhatsApp%20Image%202020-07-23%20at%2019.27.59](https://github.com/divyanshutw/DesiMarketplace/blob/master/Images/WhatsApp%20Image%202020-07-23%20at%2019.27.59.jpeg)
   - 2.1 The *i* button displays details about the app.
   - 2.2 The *logout* is to logout from the current account.
   - 2.3 The *Profile* button enable the user to edit his details.
   - 2.4 The *Chats* button will open up an activity containing all the chats. Next, clicking on any chat, the complete realtime chat activity will open up.
   - 2.5 The *Orders* button will open up an activity that contains the order history with each individual user.
   - 2.6 The *Search* button will open up the search choice activity in which the user has to chose what to search, filters and sorting order(by rating or by distance). Proceeding further the user will get the search results and clicking on any search will open the sellerPage<`3`>  activity.
   - 2.7 The *Book* button will open the books. Clicking on any book will open up a realtime BahiKhata.
   - 2.8 The *Favourites* button will open up an activity that contains a list of all the sellers marked as favourites. Clicking on any one will open up the sellerPage<`3`> activity.
   - 2.9 The *News* button will open up an activity similar to a news app.
  
3. The sellerPage activity will open up as shown:
  - ![WhatsApp%20Image%202020-07-23%20at%2019.29.39](https://github.com/divyanshutw/DesiMarketplace/blob/master/Images/WhatsApp%20Image%202020-07-23%20at%2019.29.39.jpeg)
  
  ---
  
## Installation instructions:
  - The developers can clone the repository.
    - Add the project to firebase. All dependencies(FirebaseFirestore,FirebaseAuth,FirebaseStorage) have already been added to the gradle file
    - Add the google maps API key to your project [Refer to documentation](https://developers.google.com/maps/documentation/android-sdk/start)
  - Now you can run the project on Android Studio.
  - Others can download the apk from the given link.(apk will be uploaded on 27-07-2020)

## Known bugs:
 - Maps activity crashing sometimes
 - Filters (boolean payment,delivery) not saving in firestore
 
### Working on:
 Search Activity
 Chat Activity
