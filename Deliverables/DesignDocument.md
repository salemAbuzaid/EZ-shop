# Design Document 


Authors: Salem Mohamed Salem Metwaly Abouzaid, Patrizio de Girolamo, Giulia Medde, Carlo Vitale

Date: 19/05/2021

Version: 3


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 
Architettural patterns: MVC, Facade

Package diagram  
======
```plantuml
@startuml
package "EZShop"{
    component [Application] as App
}
package "EZShopGUI"{
    component [GUI] as GUI
}
package "EZShopExceptions"{
    component [Exceptions] as Exc
}
App -> GUI
App -down-> Exc
@enduml
```

# Low level design
EZShop package:
<img src="Diagram/class_diagram3.jpg" alt="drawing">


# Verification traceability matrix
||EZShop|ProductType|Position|Products|SaleTransaction|SaleTransactions|LoyaltyCard|Customer|Customers|ReturnTransaction|Order|Orders|User|Users|BalanceOperation|AccountBook|CreditCardCircuit|
|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|
| FR1 | X |  |  |  |  |  |  |  |  |  |  |  | X | X |  |  |  |
| FR3 | X | X | X | X |  |  |  |  |  |  |  | X | X |  |  |  |  |
| FR4 | X | X | X | X |  |  |  |  |  |  | X | X | X | X |  |  |  |
| FR5 | X |  |  |  |  |  | X | X | X |  |  |  | X | X |  |  |  |
| FR6 | X | X | X | X | X | X |  |  |  | X |  |  | X | X |  |  |  |
| FR7 | X |  |  |  |  |  |  |  |  |  |  |  | X | X |  |  | X |
| FR8 | X |  |  |  |  |  |  |  |  |  |  |  | X | X | X | X |  |

# Verification sequence diagrams 

Sequence diagram for each key scenario  

Scenario 1.1 - Create new product type
======
```plantuml
@startuml
ShopManager -> EZShop: createProductType(...)
EZShop -> ProductType: pt = new ProductType(...)
ProductType -> Position: pos = new Position(...)
ProductType -> EZShop: success
EZShop -> Products: addProduct(pt)
Products -> EZShop: success
EZShop -> ShopManager: success
@enduml
```
Scenario 2.1 - Create new user
======
```plantuml
@startuml
title Scenario 2.1
Admin -> EZShop: createUser(...)
EZShop -> User: u = new User(...)
EZShop -> Users: addUser(u)
Users -> EZShop: success
EZShop -> Admin: success
@enduml
```
Scenario 3.1 - Order issued
======
```plantuml
@startuml
ShopManager-> EZShop: issueOrder(productBarcode)
EZShop -> Products: getProductByBarcode(productBarcode)
Products -> EZShop: ProductType p
EZShop -> Order: o = new Order(p,...)
EZShop -> Order: o.updateStatus("ISSUED")
EZShop -> Orders: addOrder(o)
Orders -> EZShop: success
EZShop -> ShopManager: success
@enduml
```
Scenario 3.2 - Order payed  
======  
```plantuml
@startuml
ShopManager-> EZShop: payOrder(orderID)
EZShop -> Orders: getOrderByID(orderID)
Orders -> EZShop: Order o
EZShop -> Order: o.pay()
Order -> BalanceOperation: bo = new BalanceOperation(...)
Order -> BalanceOperation: bo.setAmount(quantity*pricePerUnit)
Order -> AccountBook: addTransaction(bo)
AccountBook -> Order: success
Order -> EZShop: success
EZShop -> Order: o.updateStatus("PAYED")
EZShop -> ShopManager: success
@enduml
```
Scenario 3.3 - Order arrived  
======  
```plantuml
@startuml
ShopManager-> EZShop: recordOrderArrival(orderID)
EZShop -> Orders: getOrderByID(orderID)
Orders -> EZShop: Order o
EZShop -> Order: o.getProductType()
Order -> EZShop: ProductType pt
EZShop -> ProductType: pt.checkPosition()
ProductType -> EZShop: valid position
EZShop -> ProductType: pt.increaseQuantity(o.quantity)
ProductType -> EZShop: success
EZShop -> Order: o.updateStatus("COMPLETED")
EZShop -> ShopManager: success
@enduml
```
Scenario 4.1 - Create customer
===========
```plantuml
@startuml
Cashier -> EZShop: defineCustomer()
EZShop -> Customer: c = new Customer(...)
EZShop -> Customers: addCustomer(c)
Customers -> EZShop: success
EZShop -> Cashier: success
@enduml
```

scenario 4.2 - Attach Loyalty Card to customer
====
```plantuml
Cashier -> EZShop :getCustomer(id)
EZShop -> Customers :getCustomerById(id)
Customers -> EZShop :customer c
EZShop -> Cashier :customer c
Cashier -> EZShop: attachCardToCustomer(cardCode,c.id)
EZShop -> Customer :attachLoyaltyCard(cardCode,c.id)
Customer -> LoyalityCard :new LoyaltyCard(cardCode)
Customer <- LoyalityCard :Loyalty.ID
EZShop <- Customer :Success
Cashier <- EZShop :Success
@enduml
```

Scenario 5.1 - Login
=======
```plantuml
@startuml
Cashier -> EZShop :login(usernm,pwd)
EZShop -> Users :findUser(usrnm,pwd)
Users -> EZShop :user u
EZShop -> EZShop :loggedUser = setLoggedIn(u)
EZShop -> Cashier :return loggedUser
@enduml
```

Scenario 5.2 - Logout
=======
```plantuml
@startuml
Cashier -> EZShop: logout()
EZShop -> EZShop: setLoggedOut()
EZShop -> Cashier: success
@enduml
```

Scenario 6.4 - Sale of product type X with Loyalty Card update  
======  
```plantuml
@startuml
Cashier-> EZShop: ID = startSaleTransaction()
EZShop -> SaleTransaction: s = new SaleTransaction(...)
SaleTransaction -> ProductType: addProduct&Quantity()
SaleTransaction -> ProductType: decreaseQuantity()  
ProductType -> SaleTransaction: return true 
Cashier-> EZShop: endSaleTransaction(ID)
Cashier -> EZShop: receiveCreditCardPayment(ID,creditcard)
EZShop -> SaleTransaction: s.validateCreditCard(creditcard)
SaleTransaction -> EZShop: success 
EZShop -> SaleTransaction: s.endSale("credit card")
SaleTransaction -> SaleTransaction: pts = computePoints()
SaleTransaction -> SaleTransaction: amount = computeAmount()
SaleTransaction -> S7.1: goto(amount)  
S7.1 -> SaleTransaction: success 
SaleTransaction -> LoyaltyCard: updatePoints(pts)
SaleTransaction -> EZShop: success
EZShop -> SaleTransactions: addTransaction(s)  
EZShop -> Cashier: success
@enduml
```

Scenario 6.5 - Sale of product type X cancelled  
======  
```plantuml
@startuml
Cashier-> EZShop: ID = startSaleTransaction()
EZShop -> SaleTransaction: s = new SaleTransaction(...)
SaleTransaction -> ProductType: addProduct&Quantity()
SaleTransaction -> ProductType: decreaseQuantity()  
ProductType -> SaleTransaction: return true 
Cashier-> EZShop: endSaleTransaction()
Cashier -> EZShop: receiveCreditCardPayment(ID,creditcard)
EZShop -> SaleTransaction: s.validateCreditCard(creditcard)
SaleTransaction -> EZShop: success 
EZShop -> SaleTransaction: s.endSale("credit card")
SaleTransaction -> SaleTransaction: amount = computeAmount()
SaleTransaction -> S7.1: goto(amount)  
S7.1 -> SaleTransaction: failed  
SaleTransaction -> ProductType: increaseQuantity()
SaleTransaction -> EZShop: failed
EZShop -> Cashier: failed
@enduml
```

Scenario 6.6 - Sale of product type X completed(Cash)  
======  
```plantuml
@startuml
Cashier-> EZShop: ID = startSaleTransaction()
EZShop -> SaleTransaction: s = new SaleTransaction(...)
SaleTransaction -> ProductType: addProduct&Quantity()
SaleTransaction -> ProductType: decreaseQuantity()  
ProductType -> SaleTransaction: return true 
Cashier-> EZShop: endSaleTransaction(ID)
Cashier -> EZShop: receiveCashPayment(ID,cash)
EZShop -> SaleTransaction: s.endSale("cash")
EZShop -> SaleTransaction: s.showSale()
SaleTransaction -> SaleTransaction: amount = s.computeAmount()  
SaleTransaction -> S7.4: goto(amount)  
S7.4 -> SaleTransaction: success
EZShop -> SaleTransactions: addTransaction(s)  
EZShop -> SaleTransaction: change = s.computeChange(cash)
EZShop -> Cashier: return change
@enduml
```

Scenario 7.1 - Manage payment by valid credit card 
======  
```plantuml
@startuml
SaleTransaction -> BalanceOperation: bo = new BalanceOperation(...)
SaleTransaction -> BalanceOperation: bo.setAmount(quantity*pricePerUnit)
BalanceOperation -> CreditCardCircuit: startCCtransaction()
CreditCardCircuit -> BalanceOperation:  success 
BalanceOperation -> SaleTransaction: success
SaleTransaction -> AccountBook: addTransaction(bo)
AccountBook -> SaleTransaction: success
@enduml
```
Scenario 7.4 - Manage cash payment 
======  
```plantuml
@startuml
SaleTransaction -> BalanceOperation: bo = new BalanceOperation(...)
SaleTransaction -> BalanceOperation: bo.setAmount(quantity*pricePerUnit)
BalanceOperation -> SaleTransaction: success
SaleTransaction -> AccountBook: addTransaction(bo)
AccountBook -> SaleTransaction: success
@enduml
```

Scenario 8.1 - Return transaction of product type X completed, credit card
======  
```plantuml
@startuml
Cashier-> EZShop: getSaleTransaction(transactionID)
EZShop -> SaleTransactions: getTransactionByID(transactionID)
SaleTransactions -> EZShop: currentTransaction
EZShop -> Cashier: currentTransaction

Cashier -> EZShop: startReturnTransaction(currentTransaction.id)
EZShop -> ReturnTransaction: retTransaction = new ReturnTransaction(...) 
ReturnTransaction -> EZShop: return retTransaction
EZShop -> Cashier: id = retTransaction.id

Cashier -> EZShop: getProductTypeByBarCode(barcode)
EZShop -> Products: Product p = getProductTypeByBarcode(barcode)
Products -> EZShop: return p
EZShop -> Cashier: return p

Cashier -> EZShop: returnProduct(id, p.barcode, N)
EZShop -> EZShop: retTransaction.setProductType(p)
EZShop -> EZShop: retTransaction.setQuantity(N)
EZShop -> Cashier: success

Cashier -> EZShop: endReturnTransaction(id, true)
EZShop -> EZShop: retTransaction.commit()
EZShop -> EZShop: p.increaseQuantity(N)
EZShop -> Cashier: success

Cashier -> S10.1: manageCreditCardPayment
S10.1 -> Cashier: success
@enduml
```

Scenario 8.2 - Return transaction of product type X completed, cash
======  
```plantuml
@startuml
Cashier-> EZShop: getSaleTransaction(transactionID)
EZShop -> SaleTransactions: getTransactionByID(transactionID)
SaleTransactions -> EZShop: currentTransaction
EZShop -> Cashier: currentTransaction

Cashier -> EZShop: startReturnTransaction(currentTransaction.id)
EZShop -> ReturnTransaction: retTransaction = new ReturnTransaction(...) 
ReturnTransaction -> EZShop: return retTransaction
EZShop -> Cashier: id = retTransaction.id

Cashier -> EZShop: getProductTypeByBarCode(barcode)
EZShop -> Products: Product p = getProductTypeByBarcode(barcode)
Products -> EZShop: return p
EZShop -> Cashier: return p

Cashier -> EZShop: returnProduct(id, p.barcode, N)
EZShop -> EZShop: retTransaction.setProductType(p)
EZShop -> EZShop: retTransaction.setQuantity(N)
EZShop -> Cashier: success

Cashier -> EZShop: endReturnTransaction(id, true)
EZShop -> EZShop: retTransaction.commit()
EZShop -> EZShop: p.increaseQuantity(N)
EZShop -> Cashier: success

Cashier -> EZShop: receiveCashPayment(ID,cash)
EZShop -> ReturnTransaction: retTransaction.endSale("cash")
EZShop -> ReturnTransaction: retTransaction.showSale()
ReturnTransaction -> ReturnTransaction: amount = retTransaction.computeAmount()  
ReturnTransaction -> S10.2: goto(amount)  
S10.2 -> ReturnTransaction: success
EZShop -> ReturnTransaction: change = retTransaction.computeChange(cash)
EZShop -> Cashier: return change

@enduml
```

Scenario 9.1 - List credits and debits
======  
```plantuml
@startuml
Manager -> EZShop: getCreditsAndDebits(LocalDate from, LocalDate to)
EZShop -> AccountBook: getListBalanceOperationsByDate()
AccountBook -> EZShop: return listBalanceOperations
EZShop -> Manager: return listBalanceOperations
@enduml
```
Scenario 10.1 - Return payment by  credit card
======  
```plantuml
@startuml
Cashier -> EZShop: receiveCreditCardPayment(id,creditcard)
EZShop -> ReturnTransaction: retTransaction.validateCreditCard(creditcard)
ReturnTransaction -> EZShop: success 
EZShop -> ReturnTransaction: retTransaction.endReturn("credit card")
ReturnTransaction -> ReturnTransaction: amount = computeAmount()

ReturnTransaction -> BalanceOperation: bo = new BalanceOperation(...)
ReturnTransaction -> BalanceOperation: bo.setAmount(quantity*pricePerUnit)
ReturnTransaction -> CreditCardCircuit: startCCtransaction()
CreditCardCircuit -> BalanceOperation:  success 
BalanceOperation -> ReturnTransaction: success
ReturnTransaction -> AccountBook: addTransaction(bo)
AccountBook -> ReturnTransaction: success
ReturnTransaction -> EZShop: success
EZShop -> Cashier: success
@enduml
```
