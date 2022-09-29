## The Gilded Rose Expands

The Gilded Rose* is a small inn in a prominent city that buys and sells only the finest items. The shopkeeper, Terry, is looking to expand by providing merchants in other cities with access to the shop's inventory via a public HTTP-accessible API. The Gilded Rose would like to utilize a surge pricing model like `Uber` to increase the price of each item by 10% if it has been viewed more than 10 times in an hour.

### API requirements
- Retrieve the current inventory (i.e. list of items)
- View an individual Item by name
- Buy an Item
  - authentication needed

**Here is the definition for an item:**

```java
class Item {
    public String name;
    public String description;
    public int price;
}
```

**A couple questions to consider:**
1. How do we know if a user is authenticated?
2. Is it always possible to buy an item?

### Deliverables

- [ ] The project should run and build in your IDE, by command line, and by java -jar <your-jar>.
- [ ] Complete this skeleton project with the required functionality to achieve the API requested.
- [ ] Use the simple H2 in memory database that is already configured.
- [ ] A system that can process the two API requests via HTTP.
- [ ] An authentication system.
- [ ] Include unit and integration tests.
- [ ] The code should be published in a public accessible repository, so we can clone and review it.

### Summary

Prepare a little summary of your application [here](SUMMARY.md).

- [ ] Describe and explain your surge pricing design.
- [ ] Describe your API endpoints design and model. Include one example of a request and response for each API call.
- [ ] Explain the authentication mechanism you chose, and why.
- [ ] Explain your testing, what you covered, and why.
- [ ] Describe the features you would include if this was an actual business application. 

### Provided Resources

* A simple Maven-managed project configuration
* Pre-configured Spring Boot Application with the basics and an in-memory DB
* Use this JDK 11 binary if you don't have one
  * Linux: [tar.gz](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1%2B1/OpenJDK11U-jdk_x64_linux_hotspot_11.0.14.1_1.tar.gz)
  * MacOS: [pkg](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1%2B1/OpenJDK11U-jdk_x64_mac_hotspot_11.0.14.1_1.pkg) or [tar.gz](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1+1/OpenJDK11U-jdk_x64_mac_hotspot_11.0.14.1_1.tar.gz)
  * Windows: [msi](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1+1/OpenJDK11U-jdk_x64_windows_hotspot_11.0.14.1_1.msi) or [zip](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1+1/OpenJDK11U-jdk_x64_windows_hotspot_11.0.14.1_1.zip)

### FAQ
* Include any project dependencies you need, i.e. lombok, apache commons
* The surge price mechanism is open-ended. Your job is to create it and explain it.

---
