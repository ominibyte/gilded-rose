Surge Pricing Design
====================

I created a `PricingModifier` interface with a concrete implementation for surge pricing in `SurgePricingModifier`. This concrete implementation is easily customizable using three properties: pricing-percent-adjustment, views-threshold, duration-threshold from either the command line, `application.yml` or similar for any environment.
By default, the application is started with a surge pricing of 10% increase when an item is viewed more than 10 times in a hour.

The `SurgePricingModifier` internally uses an implementation of `ItemViewCacheProvider` which maintains a cache of access times for each item using Hazelcast and is injected into the `InventoryService`.
Based on the configured properties for the `SurgePricingModifier`, the pricing for each item is returned from the `InventoryService` as a function of the access times.

A Filter was created called `ItemViewMetricFilter` which listens for requests to view an individual item by name and updates the items access cache. 
The process of updating the cache involves invalidating the cache to remove older entries and adds the current view to the list. A scheduler could have also been used to do the invalidating but this was easy enough to get the job done.
Also, a `HandlerInterceptor` could have also been considered but the requirement was simple enough to use a Filter.

A decision was made to only increment access count on an Item when we view an individual item by name. 
Following the same process above, it is possible to add any other endpoint to the list that increments access count for each item.

The decision to leverage Hazelcast as the caching layer was because it was easy to include as a Java library, and it supports distributed mode. 
Other options like Redis could have also been used as well.

API Endpoint Design and Model
=============================

All endpoints were chosen to return a JSON-encoded response because Springboot supports it natively, and it is a widely used format.

The `InventoryController` receives the request and delegates the task to the `InventoryService` which makes most of the decisions which could involve accessing the database using the `ItemRepository` to query for `Item`s.
The responses are mapped from `Item`(s) to `SurgeItem`(s), which is a DTO with possible surge pricing based on the number of access.

The `Item` entity models the database table for items in Terry's shop inventory. An additional `nameCase` column was added to enable easy case-insensitive matching of items by name.

There are three endpoints in the inventory controller application:

getAllItems (GET /inventory/items)
---

This retrieves all the items in the inventory.
Here's a sample response from the API:

```json
[
    {
        "id": 1,
        "name": "Silk",
        "description": "Silk material",
        "price": 55,
        "quantity": 1
    },
    {
        "id": 2,
        "name": "Cotton",
        "description": "Cotton wool",
        "price": 90,
        "quantity": 36
    },
    {
        "id": 3,
        "name": "Linen",
        "description": "Linen clothing",
        "price": 133,
        "quantity": 18
    }
]
```

getItemByName (GET /inventory/items/{name})
---

This retrieves an item in the database that matches exactly (case-insensitively) to an item in the inventory.
The user can supply the name of an item to the path variable `{name}`. 

If a match is found, here is a sample response from the API for a call to:

`GET /inventory/items/silk`

```json
{
    "id": 1,
    "name": "Silk",
    "description": "Silk material",
    "price": 55,
    "quantity": 1
}
```

A status code of 200 is returned when a match is found.

When a match is not found, a status code of 404 is returned with a payload. Here's an example:

`GET /inventory/items/silks`

```json
{
    "message": "Unable to find an item with the name - \"silks\"",
    "status": false
}
```


purchaseItem (POST /inventory/purchase/{id})
---

Protected endpoint used to buy an item.

If the item exists and has sufficient quantity left, then a status code of 200 is return. As an example:

`POST /inventory/purchase/1`

```json
{
  "id": 1,
  "name": "Silk",
  "description": "Silk material",
  "price": 55,
  "quantity": 0
}
```

The purchased item is returned showing the current quantity available.

The purchase process is handled as a transaction on the database which decrements the quantity available in case of race conditions.

Ideally, we might want to have an `Orders` table with information about all purchased items but to keep this simple, 
I decided to ignore that feature primarily due to the fact that no additional use of the purchased information is mentioned.

Authentication Mechanism
=========

Mainly, for simplicity, I chose to make use of HTTP Basic Auth. In a bigger application, I would consider using OAuth2 with either JWT or authentication services like Auth0.
However, that would have increased the scope of the work to include generating and validating the tokens.

For testing purposes, there is a user already created with the credentials:

Username: `admin` <br/>
Password: `hidden`


Testing
=======

I wrote a number of Unit testing to validate all the interesting paths as well as edge cases in the application and wrote Integration test to verify the end-to-end business process.

The unit tests are to ensure that the behaviour of the units are on par with what is expected while the Integration test is to ensure that all the unit working together behave correctly.


Features for the future
====================

1. As a stateless application, having the user supply their login credentials each time they need to make a purchase is not the best design. Changing the authentication to use OAuth2 tokens would be the first step to improving the authtication flow.
2. I would like to create an `Orders` table to model the purchase order of a user and store the successful purchases in the database with additional metadata.
3. A payment processing system where the user can provide their card information before making a purchase.
