package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class EZShop implements EZShopInterface {

    private DBClass db;

    private SaleTransactions saleTransactions;
    private Users users;
    private Products products;
    private Customers customers;
    private Orders orders;
    private AccountBook accountbook;
    private CreditCards creditCards;

    private UserClass loggedUser = null;
    private SaleTransactionClass currentTransaction = null;
    private ReturnTransactionClass currentReturnTransaction;
    private ReturnTransactionRepository returnTransactionRepository;
    private ProductsRFID productsRFID;
    private boolean firstLogin = true;
    private boolean openTransaction = false;

    public boolean validRFID(String rfid) {
        return rfid != null && rfid.matches("^[0-9]{12}$");
    }

    public boolean validCreditCard(String code) {
        if (code == null || !code.matches("[0-9]{13,19}")) {
            // se in input non abbiamo un codice con solo interi e con lunghezza di 16, ritorna false
            return false;
        }
        int nDigits = code.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = code.charAt(i) - '0';
            if (isSecond)
                d = d * 2;
            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }


    public boolean barcodeValidator(String barCode) {
        if (barCode == null || !barCode.matches("[0-9]{12,14}")) {
            // se in input non abbiamo un codice con solo interi e con lunghezza compresa
            // tra 12 e 14 inclusi, ritorna false
            return false;
        }
        int codeLength = barCode.length();
        int parity = codeLength % 2;
        int digits[] = new int[codeLength - 1];
        char barCode_arr[] = barCode.toCharArray();
        // converti la stringa in un vettore di interi da 0 a 9
        for (int i = 0; i < codeLength - 1; i++) {
            digits[i] = Character.getNumericValue(barCode_arr[i]);
        }
        int sum = 0;
        for (int i = 0; i < codeLength - 1; i++) {
            sum += ((i % 2 == parity) ? (digits[i] * 3) : digits[i]);
        }
        int i = 0;
        // prendi il multiplo di 10 uguale o subito più grande di sum
        while (i < sum) {
            i += 10;
        }
        // sottrai sum a questo numero
        int checkVal = i - sum;
        // confronta il risultato con l'ultima cifra del codice a barre
        return (checkVal == Character.getNumericValue(barCode_arr[codeLength - 1]));
    }

    public CreditCards importCreditCards() {
        creditCards = new CreditCards();

        try (FileReader f = new FileReader("creditcards.txt")) {
            BufferedReader b;
            b = new BufferedReader(f);
            String s;

            while (true) {
                s = b.readLine();
                if (s == null)
                    break;

                if (!s.contains("#")) {
                    String[] strings = s.split(";");
                    CreditCard c = new CreditCard(strings[0], Double.parseDouble(strings[1]));
                    creditCards.addCreditCard(c);
                }
            }
            return creditCards;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void importData() {
        db = new DBClass();
        users = db.importUsers();
        products = db.importProducts();
        orders = db.importOrders();
        saleTransactions = db.importTransactions();
        returnTransactionRepository = db.importReturnTransactions();
        customers = db.importCustomers();
        accountbook = db.importAccounting();
        productsRFID = db.importProductsRFID();

    }

    public void resetID() {
        products.giveNewId();
        saleTransactions.setNewId();
        returnTransactionRepository.setNewId();
        accountbook.setNewId();
    }


    @Override
    public void reset() {
        importData();
        db.deleteProducts();
        db.deleteSaleTransactions();
        db.deleteReturnTransactions();
        accountbook.setCurrentBalance(0.0);
        db.deleteAccounting();
        db.deleteUsers();
        db.deleteCustomers();
        db.deleteOrders();
        db.deleteProductsRFID();
        resetID();
        loggedUser = null;
        currentTransaction = null;
        currentReturnTransaction = null;
        firstLogin = true;
        openTransaction = false;
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        importData();
        if (username == null || username.isEmpty())
            throw new InvalidUsernameException("Invalid username. Please, try again. ");

        if (password == null || password.isEmpty())
            throw new InvalidPasswordException("Invalid password. Please, try again. ");

        else if ((role == null) || role.isEmpty() || (!(role.equals("Administrator")) && !(role.equals("Cashier")) && !(role.equals("ShopManager"))))
            throw new InvalidRoleException("Invalid role. Please, try again. ");

        else if (users.getUserByUsername(username) != null)
            return -1;

        else {
            UserClass new_user = new UserClass(users.getNewUserID(), username, password, role);
            if (users.addUser(new_user)) {
                if (db.addUser(new_user))
                    return new_user.getId();
                else
                    return -1;
            } else
                return -1;
        }
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (!(loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (id == null || id <= 0)
            throw new InvalidUserIdException("Invalid userID. Please, try again. ");
        else if (users.getUserById(id) == null)
            return false;
        else if (users.removeUser(id))
            return db.removeUser(id);
        else
            return false;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (!(loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (users.getUsers().isEmpty())
            return null;
        else
            return users.getUsers();
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (!(loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if ((id == null) || (id <= 0))
            throw new InvalidUserIdException("Invalid userID. Please, try again. ");
        else
            return users.getUserById(id);
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if (!(loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if ((id == null) || (id <= 0))
            throw new InvalidUserIdException("Invalid userID. Please, try again. ");
        else if ((role == null) || role.isEmpty() || (!(role.equals("Administrator")) && !(role.equals("Cashier")) && !(role.equals("ShopManager"))))
            throw new InvalidRoleException("Invalid role. Please, try again. Roles can be Cashier or ShopManager or Administrator. ");
        else if (users.getUserById(id) != null) {
            if (db.updateUserRights(id, role)) {
                users.getUserById(id).setRole(role);
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if (firstLogin) {
            importData();
            firstLogin = false;
        }
        importCreditCards();
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Invalid username. Please, try again. ");
        } else if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Invalid password. Please, try again. ");
        } else if (!(users.checkCredentials(username, password))) {
            return null;
        } else if (loggedUser != null) {
            return null;
        } else {

            loggedUser = users.getUserByUsername(username);
            return loggedUser;
        }
    }

    @Override
    public boolean logout() {
        if (loggedUser == null)
            return false;
        else {
            loggedUser = null;
            return true;
        }
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if ((!loggedUser.getRole().equals("Administrator")) && (!loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account. ");
        else if (description == null || description.isEmpty())
            throw new InvalidProductDescriptionException("Invalid description. Please, try again. ");
        else if (productCode == null || (productCode.isEmpty()) || (!barcodeValidator(productCode)))
            throw new InvalidProductCodeException("Invalid barcode. Please, try again. ");
        else if (pricePerUnit <= 0)
            throw new InvalidPricePerUnitException("Invalid price. Please, try again. ");
        else if (products.getProductTypeByBarCode(productCode) != null)
            return -1;
        else {
            ProductTypeClass new_productType = new ProductTypeClass(products.updateNewId(), productCode, description, pricePerUnit, note);
            if (products.addProduct(new_productType)) {
                if (db.addProductType(new_productType)) {
                    return new_productType.getId();
                } else
                    return -1;
            } else {
                return -1;
            }
        }
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if ((!loggedUser.getRole().equals("Administrator")) && (!loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account. ");
        else if ((id == null) || (id <= 0))
            throw new InvalidProductIdException("Invalid productID. Please, try again. ");
        else if ((newDescription == null) || (newDescription.isEmpty()))
            throw new InvalidProductDescriptionException("Invalid description. Please, try again. ");
        else if ((newCode == null) || (newCode.isEmpty()) || (!barcodeValidator(newCode)))
            throw new InvalidProductCodeException("Invalid barcode. Please, try again. ");
        else if (newPrice <= 0)
            throw new InvalidPricePerUnitException("Invalid price. Please, try again. ");
        else if (products.getProductTypeById(id) == null)
            return false;
        else if (products.getProductTypeByBarCode(newCode) != null && !products.getProductTypeByBarCode(newCode).getId().equals(id))
            return false;
        else {
            products.getProductTypeById(id).setBarCode(newCode);
            products.getProductTypeById(id).setProductDescription(newDescription);
            products.getProductTypeById(id).setPricePerUnit(newPrice);
            products.getProductTypeById(id).setNote(newNote);

            return db.updateProductType(id, newDescription, newCode, newPrice, newNote);
        }
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator account. ");
        else if ((!loggedUser.getRole().equals("Administrator")) && (!loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account. ");
        else if ((id == null) || (id <= 0))
            throw new InvalidProductIdException("Invalid productID. Please, try again. ");
        else if (products.getProductTypeById(id) == null)
            return false;
        else {
            if (products.removeProduct(products.getProductTypeById(id))) {
                return db.deleteProductType(id);
            } else {
                return false;
            }
        }
    }


    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        return products.getProductTypeList();
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or Shopmanager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or Shopmanager account.");
        else if (barCode == null || barCode.equals("") || !barcodeValidator(barCode))
            throw new InvalidProductCodeException("Invalid productCode. Please, try again. ");
        else
            return products.getProductTypeByBarCode(barCode);
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        if (description == null)
            return products.getProductsByDescription("");
        else
            return products.getProductsByDescription(description);
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        if (productId == null || productId <= 0)
            throw new InvalidProductIdException("Invalid productID. Please, try again.");
        else if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        ProductTypeClass p = this.products.getProductTypeById(productId);
        if (p == null || p.getLocation().equals(""))
            return false;
        if (p.getQuantity() < 0)
            p.setQuantity(0);
        int finalQuantity = toBeAdded + p.getQuantity();
        if (finalQuantity < 0)
            return false;
        else {
            if (db.productsUpdateQuantity(productId, finalQuantity)) {
                p.setQuantity(finalQuantity);
                return true;
            } else
                return false;
        }
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (productId == null || productId <= 0)
            throw new InvalidProductIdException("Invalid productId. Please, try again.");

        ProductTypeClass p = this.products.getProductTypeById(productId);
        if (newPos == null) {
            p.setLocation("");
            return false;
        }

        String[] strings = newPos.split("-");
        if (strings.length != 3) {
            throw new InvalidLocationException("location format is not valid");
        }

        if (!newPos.matches("[0-9]+-[a-zA-Z]-[0-9]+"))
            throw new InvalidLocationException("location subformat is not valid");


        if (newPos.equals("") || this.products.getProductsByLocation().containsKey(newPos)) {
            p.setLocation("");
            return false;
        } else {
            p.setLocation(newPos);
            return db.productsUpdatePosition(productId, newPos);
        }
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        if (productCode == null || productCode.equals("") || !barcodeValidator(productCode))
            throw new InvalidProductCodeException("Invalid productCode. Please, try again.");
        if (quantity <= 0)
            throw new InvalidQuantityException("Invalid quantity. Please, try again. Quantity must be >0. ");
        if (pricePerUnit <= 0)
            throw new InvalidPricePerUnitException("Invalid pricePerUnit. Please, try again. PricePerUnit must be >0. ");
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");

        if (this.products.getProductTypeByBarCode(productCode) == null)
            return -1;

        //nell order ISSUE non assegno una balance operation
        OrderClass order = new OrderClass(orders.getNewId(), pricePerUnit, quantity, "ISSUED", -1, productCode);
        this.db.addOrder(order);
        this.orders.addOrder(order);

        //controlla se l'ordine è aggiunto
        return this.orders.giveId();
    }


    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        if (productCode == null || productCode.equals("") || !barcodeValidator(productCode))
            throw new InvalidProductCodeException("Invalid productCode. Please, try again.");
        if (quantity <= 0)
            throw new InvalidQuantityException("Invalid quantity. Please, try again. Quantity must be >0.");
        if (pricePerUnit <= 0)
            throw new InvalidPricePerUnitException("Invalid pricePerUnit. Please, try again. PricePerUnit must be >0.");
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");

        else if (this.getProductTypeByBarCode(productCode) == null)
            return -1;
        else if (this.computeBalance() < quantity * pricePerUnit)
            return -1;

        OrderClass order = new OrderClass(orders.getNewId(), pricePerUnit, quantity, "ISSUED", -1, productCode);
        if (db.addOrder(order)) {
            orders.addOrder(order);
            this.recordBalanceUpdate(-quantity * pricePerUnit);
            int balanceId = accountbook.getNewId();
            order.setBalanceId(balanceId);
            this.db.orderUpdateBalanceID(order.getOrderId(), balanceId);
            order.setStatus("PAYED");
            db.orderUpdateStatus(order.getOrderId(), "PAYED");

            return this.orders.giveId();
        } else
            return -1;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (orderId == null || orderId <= 0)
            throw new InvalidOrderIdException("Invalid orderID. Please, try again.");

        OrderClass order = orders.getOrderById(orderId);
        if (order == null)
            return false;
        if (order.getQuantity() * order.getPricePerUnit() > computeBalance())
            return false;

        if (order.getStatus().toUpperCase().equals("ISSUED")) {
            order.setStatus("PAYED");

            if (db.orderUpdateStatus(order.getOrderId(), "PAYED")) {
                order.setStatus("PAYED");

                this.recordBalanceUpdate(-order.getQuantity() * order.getPricePerUnit());
                order.setBalanceId(accountbook.getNewId());
                return true;
            } else
                return false;
        } else return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (orderId == null || orderId <= 0)
            throw new InvalidOrderIdException("Invalid orderID. Please, try again. ");

        OrderClass order = orders.getOrderById(orderId);
        if (order == null)
            return false;
        ProductTypeClass p = products.getProductTypeByBarCode(order.getProductCode());
        String location = p.getLocation();
        if (location == null || location.equals(""))
            throw new InvalidLocationException("location is invalid");
        String[] strings = p.getLocation().split("-");
        if (strings.length != 3) {
            throw new InvalidLocationException("location format is not valid");
        }
        for (int i = 0; i < strings.length; i++) {
            if (i == 1) {
                if (!strings[i].matches("[a-zA-Z]+"))
                    throw new InvalidLocationException("location subformat is not valid");
            } else {
                if (!strings[i].matches("[0-9]+"))
                    throw new InvalidLocationException("location subformat is not valid");
            }
        }
        if (order.getStatus().equals("PAYED")) {
            int quantityOrdered = order.getQuantity();
            int quantityProduct = p.getQuantity();

            if (db.productsUpdateQuantity(p.getId(), quantityProduct + quantityOrdered)) {
                p.setQuantity(quantityProduct + quantityOrdered);
                if (db.orderUpdateStatus(order.getOrderId(), "COMPLETED")) {
                    order.setStatus("COMPLETED");
                    return true;
                } else
                    return false;
            } else
                return false;
        } else return order.getStatus().equals("COMPLETED");
    }

    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException,
            InvalidLocationException, InvalidRFIDException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (orderId == null || orderId <= 0)
            throw new InvalidOrderIdException("Invalid orderID. Please, try again. ");

        OrderClass order = orders.getOrderById(orderId);
        if (order == null)
            return false;
        ProductTypeClass p = products.getProductTypeByBarCode(order.getProductCode());
        String location = p.getLocation();
        if (location == null || location.equals(""))
            throw new InvalidLocationException("location is invalid");
        String[] strings = p.getLocation().split("-");
        if (strings.length != 3) {
            throw new InvalidLocationException("location format is not valid");
        }
        for (int i = 0; i < strings.length; i++) {
            if (i == 1) {
                if (!strings[i].matches("[a-zA-Z]+"))
                    throw new InvalidLocationException("location subformat is not valid");
            } else {
                if (!strings[i].matches("[0-9]+"))
                    throw new InvalidLocationException("location subformat is not valid");
            }
        }
        if (order.getStatus().equals("PAYED")) {
            int quantityOrdered = order.getQuantity();
            int quantityProduct = p.getQuantity();

            if (validRFID(RFIDfrom) && productsRFID.isRFIDfromValid(RFIDfrom, quantityOrdered))
                for (int i = 0; i < order.getQuantity(); i++) {
                    ProductRFID newP = new ProductRFID(RFIDfrom, p.getBarCode());
                    if (db.addProductRFID(newP))
                        productsRFID.addProductRFID(RFIDfrom, newP);
                    int intRFID = Integer.parseInt(RFIDfrom);
                    RFIDfrom = String.format("%012d", ++intRFID);
                }
            else
                throw new InvalidRFIDException("Invalid RFID or some RFIDs - starting from RFIDfrom - already used, please try again.");

            if (db.productsUpdateQuantity(p.getId(), quantityProduct + quantityOrdered)) {
                p.setQuantity(quantityProduct + quantityOrdered);
                if (db.orderUpdateStatus(order.getOrderId(), "COMPLETED")) {
                    order.setStatus("COMPLETED");
                    return true;
                } else
                    return false;
            } else
                return false;
        } else return order.getStatus().equals("COMPLETED");
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        else if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account");
        return orders.getOrdersList();
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (customerName == null || customerName.isEmpty() /* check if customer name already exists: || customers.getCustomerList().stream().anyMatch(c -> (c.getCustomerName().equals(customerName)))*/)
            throw new InvalidCustomerNameException("Invalid customer name. Please, try again.");
        else if (loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")) {
            CustomerClass c = new CustomerClass();
            c.setId(customers.getNewId());
            c.setCustomerName(customerName);
            if (db.addCustomer(c)) {
                customers.addCustomer(c.getId(), c);
                return c.getId();
            }
            return -1;
        } else
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You don't have the rights to perform this action, please login as Administrator, Cashier or ShopManager.");
        if (id == null || id <= 0)
            throw new InvalidCustomerIdException();
        if ((newCustomerCard == null) || !newCustomerCard.matches("(^$|^[0-9]{10}$)"))
            throw new InvalidCustomerCardException("Customer card number is null, no changes were applied.");
        CustomerClass c = customers.getCustomerById(id);
        if (newCustomerName == null || newCustomerName.isEmpty())
            throw new InvalidCustomerNameException("Invalid customer name. Please, try again. ");
        if (!db.updateCustomerName(c.getId(), newCustomerName))
            return false;
        else
            c.setCustomerName(newCustomerName);
        if (newCustomerCard.isEmpty()) {
            String card = c.getCustomerCard();

            if (db.updateCustomerCard(c.getId(), ""))
                if (db.updatePoints(c.getCustomerCard(), -1)) {
                    c.setCustomerCard("");
                    c.setPoints(-1);
                }
            throw new InvalidCustomerCardException("Customer card number is empty, loyalty card " + card + " has been removed from the customer " + c.getId() + ".");
        }
        if (newCustomerCard.matches("^[0-9]{10}$")) {
            //check per verificare se il newCustomerCard è già presente all'interno della lista
            if (!customers.checkIfCardExists(newCustomerCard)) {
                c.setCustomerCard(newCustomerCard);
                if (c.getPoints() == -1)
                    return db.attachCard(c.getId(), newCustomerCard);
                else
                    return db.updateCustomerCard(c.getId(), newCustomerCard);
            } else
                return false; //RETURN FALSE ANCHE SE DB UNREACHABLE
        } else return false;

    }


    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")) {
            if (id != null && id > 0) {
                CustomerClass c = customers.getCustomerById(id);
                if (c != null) {
                    customers.removeCustomer(c); //can be true either false (false if we have problems on reaching the db)
                    return db.deleteCustomer(c.getId());
                } else
                    return false;
            } else
                throw new InvalidCustomerIdException("Invalid customerID. Please, try again. CustomerID must be >0. ");
        } else
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")) {
            if (id != null && id > 0)
                return customers.getCustomerById(id); //is null if c doesn't exist
            else
                throw new InvalidCustomerIdException("Invalid customerID. Please, try again. CustomerID must be >0. ");
        } else
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")) {
            return customers.getCustomerList(); //sorta di cast da List<CustomerClass> a List<Customer>
        } else
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
    }

    @Override
    public String createCard() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        //if connection to db fails -> return "";
        if (loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")) {
            return customers.getNewValidCardCode();
        } else
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (customerId == null || customerId <= 0)
            throw new InvalidCustomerIdException("Invalid customerID. Please, try again. ");
        if (customerCard == null || customerCard.length() != 10 || !customerCard.matches("[0-9]*"))
            throw new InvalidCustomerCardException("Invalid customer card. Please, try again. ");

        CustomerClass c = customers.getCustomerById(customerId);

        if (!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (c == null || customers.checkIfCardExists(customerCard))
            return false;
        else {
            c.attachLoyaltyCard(customerCard);
            return db.attachCard(c.getId(), customerCard);
        }
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (customerCard == null || customerCard.length() != 10 || !customerCard.matches("[0-9]*"))
            throw new InvalidCustomerCardException("Invalid customer card. Please, try again. ");

        CustomerClass c = customers.getCustomerByCard(customerCard);

        if (c == null || (pointsToBeAdded < 0 && (c.getPoints() + pointsToBeAdded) < 0)) return false;

        c.updatePoints(pointsToBeAdded);
        return db.updatePoints(c.getCustomerCard(), pointsToBeAdded);
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        currentTransaction = new SaleTransactionClass();

        currentTransaction.setTicketNumber(saleTransactions.getNewId());

        openTransaction = true;
        return currentTransaction.getTicketNumber();
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException("Invalid transaction id. Please, try again. ");
        if (productCode == null || productCode.isEmpty() || !barcodeValidator(productCode))
            throw new InvalidProductCodeException("Invalid productCode. Please, try again. ");
        if (amount < 0)
            throw new InvalidQuantityException("Invalid quantity. Please, try again. Quantity must be >0. ");

        if (!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        ProductTypeClass p = products.getProductTypeByBarCode(productCode);
        if (p == null || p.getQuantity() < amount || !openTransaction) return false;

        TicketEntryClass t = new TicketEntryClass(productCode, p.getProductDescription(), amount, p.getPricePerUnit(), p.getDiscountRate());

        if (db.productsUpdateQuantity(p.getId(), p.getQuantity() - amount)) {
            currentTransaction.addNewTicketEntry(t);
            p.decreaseQuantity(amount);
            currentTransaction.updatePrice();
            return true;
        }
        return false;
    }


    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        String role = loggedUser.getRole();
        if (!(role.equals("Cashier") || role.equals("ShopManager") || role.equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException("Invalid transaction id. Please, try again. ");
        if (!validRFID(RFID))
            throw new InvalidRFIDException("Invalid RFID. Please, try again. ");
        if (productsRFID.getProductByRFID(RFID) == null)
            return false;
        if (!currentTransaction.getId().equals(transactionId))
            return false;

        String barcode = productsRFID.getProductByRFID(RFID).getBarCode();
        if (productsRFID.getProductByRFID(RFID).isSold()) return false;

        ProductTypeClass p = products.getProductTypeByBarCode(barcode);
        TicketEntryClass t = new TicketEntryClass(barcode, p.getProductDescription(), 1, p.getPricePerUnit(), p.getDiscountRate());

        if (db.productsUpdateQuantity(p.getId(), p.getQuantity() - 1)) {
            currentTransaction.addNewTicketEntry(t);
            p.decreaseQuantity(1);
            currentTransaction.updatePrice();

            productsRFID.getProductByRFID(RFID).setSold(true);
            if(db.setSoldProductRFID(productsRFID.getProductByRFID(RFID))) {
                t.addRFID(RFID);
                return true;
            }
            else{
                productsRFID.getProductByRFID(RFID).setSold(false);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        boolean deleted;
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (((loggedUser.getRole() == null) || loggedUser.getRole().isEmpty()) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        else if (productCode == null || productCode.isEmpty() || !barcodeValidator(productCode)) {
            throw new InvalidProductCodeException("Invalid productCode. Please, try again.");
        } else if (amount < 0) {
            throw new InvalidQuantityException("Invalid quantity. Please, try again. Quantity must be >0 and less than the current quantity. ");
        } else if (amount > currentTransaction.getEntryByBarCode(productCode).getAmount())
            return false;
        else if (currentTransaction == null)
            return false;
        else {
            ProductType product = products.getProductTypeByBarCode(productCode);
            if (db.productsUpdateQuantity(product.getId(), product.getQuantity() + amount)) {
                product.setQuantity(product.getQuantity() + amount);
                deleted = currentTransaction.getEntries().removeIf(ticketEntry -> ticketEntry.getBarCode().equals(productCode));

                if (deleted)
                    currentTransaction.setPrice(currentTransaction.calculatePrice());

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException {
        SaleTransactionClass st;
        ProductRFID pRFID;
        String barCode;
        boolean deleted;
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (((loggedUser.getRole() == null) || loggedUser.getRole().isEmpty()) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        }
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException("Transaction id is not valid. Please, try again. ");
        if (!validRFID(RFID))
            throw new InvalidRFIDException("RFID is invalid or it's empty");
        if (!currentTransaction.getId().equals(transactionId)) return false;
        pRFID = productsRFID.getProductByRFID(RFID);
        if (pRFID == null)
            return false;

        barCode = pRFID.getBarCode();
        if (!currentTransaction.getEntryByBarCode(barCode).getProductsRFID().contains(RFID)) return false;

        ProductType product = products.getProductTypeByBarCode(barCode);
        if (db.productsUpdateQuantity(product.getId(), product.getQuantity() + 1)) {
            product.setQuantity(product.getQuantity() + 1);

            currentTransaction.setPrice(currentTransaction.calculatePrice());
            pRFID.setSold(false);
            currentTransaction.getEntryByBarCode(barCode).removeRFID(RFID);
            db.setSoldProductRFID(pRFID);
            if (currentTransaction.getEntryByBarCode(barCode).getProductsRFID().isEmpty()) {
                currentTransaction.getEntries().removeIf(ticketEntry -> ticketEntry.getBarCode().equals(barCode));
            }
            return true;
        } else {
            pRFID.setSold(true);
            return false;
        }
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if ((loggedUser.getRole().isEmpty()) || (loggedUser.getRole() == null) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (productCode == null || productCode.isEmpty() || !barcodeValidator(productCode)) {
            throw new InvalidProductCodeException("Invalid productCode. Please, try again. ");
        } else if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        } else if (discountRate < 0 || discountRate >= 1) {
            throw new InvalidDiscountRateException("Invalid discountRate. Please, try againg. DiscountRate must be <0 and >1. ");
        } else {
            if (!openTransaction) return false;
            if (currentTransaction != null) {
                List<TicketEntry> ticketEntries = currentTransaction.getEntries();
                for (TicketEntry t : ticketEntries) {
                    if (t.getBarCode().equals(productCode)) {
                        t.setDiscountRate(discountRate);
                        currentTransaction.updatePrice();
                        //  t.setPricePerUnit(t.getPricePerUnit() - discountRate * t.getPricePerUnit());
                        // currentTransaction.updatePriceAddingDiscount(t.getBarCode());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        SaleTransactionClass st;
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole().isEmpty()) || (loggedUser.getRole() == null) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account. ");
        } else if (discountRate < 0 || discountRate >= 1) {
            throw new InvalidDiscountRateException("Invalid discountRate. Please, try again. DiscountRate must ne <0 and >1. ");
        } else if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        } else {
            if (openTransaction) {
                st = currentTransaction;
            } else {
                st = saleTransactions.getSaleTransactionById(transactionId);
            }
            if (st != null && !st.isPaid()) {
                st.setPrice(st.getPrice() - st.getPrice() * discountRate);
                st.setDiscountRate(discountRate);
                return true;
            }
        }
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        int numPoints = 0;
        double price;
        SaleTransactionClass st;
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if ((loggedUser.getRole().isEmpty()) || (loggedUser.getRole() == null) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        } else {
            if (openTransaction) {
                st = currentTransaction;
            } else {
                st = saleTransactions.getSaleTransactionById(transactionId);
            }
            if (st != null) {
                //    price = (int) Math.floor(saleTransactions.getSaleTransactionById(transactionId).getPrice());
                price = saleTransactions.getSaleTransactionById(transactionId).getPrice();
                numPoints = (int) (price / 10);
                return numPoints;
            }
        }
        return -1;
    }


    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (((loggedUser.getRole() == null) || loggedUser.getRole().isEmpty()))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((!(loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Administrator"))))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        else if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        } else {
            SaleTransactionClass st;
            if (openTransaction) {
                st = currentTransaction;
                if (st != null) {
                    if (!db.addSaleEntries(st.getEntries(), st.getTicketNumber()))
                        return false;
                    else {
                        //    st.setPrice(st.calculatePrice());
                        saleTransactions.addTransaction(st);
                        openTransaction = false;
                        if (!db.addSaleTransaction(st))
                            return false;
                        return true;
                    }
                } else
                    return false;

            } else return false;
        }
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (saleNumber == null || saleNumber <= 0) {
            throw new InvalidTransactionIdException("Invalid saleNumber. Please, try again. ");
        }
        SaleTransactionClass st = saleTransactions.getSaleTransactionById(saleNumber);
        if (st != null && !st.isPaid()) {
            if (db.deleteSaleTransaction(st)) {
                saleTransactions.removeTransaction(st);
                return true;
            } else
                return false;
        } else
            return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (((loggedUser.getRole() == null) || loggedUser.getRole().isEmpty()) || (!(loggedUser.getRole().equals("Administrator")) && !(loggedUser.getRole().equals("Cashier")) && !(loggedUser.getRole().equals("ShopManager")))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        } else if (!openTransaction) {
            return saleTransactions.getSaleTransactionById(transactionId);
        } else return null;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator or ShopManager account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) ||
                !(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager"))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (saleNumber == null || saleNumber <= 0) {
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");
        }
        SaleTransactionClass sale = saleTransactions.getSaleTransactionById(saleNumber);
        if (sale == null)
            return -1;
        if (currentReturnTransaction != null) return -1;

        if (sale.isPaid()) {
            currentReturnTransaction = new ReturnTransactionClass(returnTransactionRepository.getNewId(), 0.0);
            currentReturnTransaction.setSaleTransaction(sale);
            //  returnTransactionRepository.addReturn(currentReturnTransaction, currentReturnTransaction.getId());
            return currentReturnTransaction.getId();
        } else return -1;
    }

    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {

        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) ||
                !(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager"))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        } else if (productCode == null || productCode.isEmpty() || !barcodeValidator(productCode)) {
            throw new InvalidProductCodeException("Invalid product name. Please, try again.");
        } else if (amount <= 0) {
            throw new InvalidQuantityException("Invalid quantity. Please, try again. Quantity must be <0. ");
        } else if (returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException("invalid return Id");
        else if (currentReturnTransaction == null)
            return false;

        SaleTransactionClass st = currentReturnTransaction.getSaleTransaction();

        if (!currentReturnTransaction.getId().equals(returnId))
            return false;

        if ((st == null) || (st.getEntryByBarCode(productCode) == null))
            return false;

        //    t.setAmount(amount); st.getEntryByBarCode(productCode);

        if (products.getProductTypeByBarCode(productCode) == null || st.getEntryByBarCode(productCode).getAmount() < amount)
            return false;

        TicketEntryClass t = new TicketEntryClass(productCode, st.getEntryByBarCode(productCode).getProductDescription(), amount, st.getEntryByBarCode(productCode).getPricePerUnit(), st.getEntryByBarCode(productCode).getDiscountRate());
    /*    if(t == null)
            return false; */
        t.setAmount(amount);
        currentReturnTransaction.addEntry(productCode, t);
        currentReturnTransaction.setPrice(currentReturnTransaction.getPrice() + (t.getPricePerUnit() * amount));
        st.setPrice(st.getPrice() - (t.getPricePerUnit() * amount));
        return true;

    }

    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) ||
                !(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Cashier"))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException("invalid return Id");
        else if (!validRFID(RFID))
            throw new InvalidRFIDException("invalid RFID. Please try again!");
        if (currentReturnTransaction == null) return false;

        SaleTransactionClass st = currentReturnTransaction.getSaleTransaction();

        if (!currentReturnTransaction.getId().equals(returnId))
            return false;

        ProductRFID pRFID = productsRFID.getProductByRFID(RFID);
        if (pRFID == null || !pRFID.isSold())
            return false;

        String productCode = pRFID.getBarCode();
        if (productCode == null)
            return false;

        if ((st == null) || (st.getEntryByBarCode(productCode) == null))
            return false;

        int amount = 1;

        TicketEntryClass t = new TicketEntryClass(productCode, st.getEntryByBarCode(productCode).getProductDescription(), amount, st.getEntryByBarCode(productCode).getPricePerUnit(), st.getEntryByBarCode(productCode).getDiscountRate());

        currentReturnTransaction.addEntry(productCode, t);
        currentReturnTransaction.setPrice(currentReturnTransaction.getPrice() + (t.getPricePerUnit() * amount));
        st.setPrice(st.getPrice() - (t.getPricePerUnit() * amount));
        pRFID.setSold(false);
        if(db.setSoldProductRFID(pRFID)) {
            return true;
        }
        else{
            pRFID.setSold(true);
            return false;
        }
    }


    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) ||
                !(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager"))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (returnId == null || returnId <= 0 || !currentReturnTransaction.getId().equals(returnId)) {
            throw new InvalidTransactionIdException("Invalid returnID. Please, try again. ");
        }
        SaleTransactionClass st = currentReturnTransaction.getSaleTransaction();
        if (commit) {
            if (currentReturnTransaction == null || st == null) return false;
            else {
                if (db.insertReturnTransaction(currentReturnTransaction, returnId)) {
                    for (TicketEntry t : currentReturnTransaction.getEntriesList()) {
                        int finalquantity = t.getAmount() + products.getProductTypeByBarCode(t.getBarCode()).getQuantity();
                        db.productsUpdateQuantity(products.getProductTypeByBarCode(t.getBarCode()).getId(), finalquantity);
                        TicketEntry respectiveTicket = st.getEntryByBarCode(t.getBarCode());
                        respectiveTicket.setAmount(respectiveTicket.getAmount() - t.getAmount());
                    }
                    returnTransactionRepository.addReturn(currentReturnTransaction, returnId);
                    currentReturnTransaction = null;
                    return true;
                } else {
                    currentReturnTransaction = null;
                    return false;
                }

          /*      currentReturnTransaction.updateSaleEntries(true);
                st.setPrice(st.calculatePrice());
                currentReturnTransaction.updateProductQuantities(products, true);
                currentReturnTransaction.setPrice(currentReturnTransaction.calculateTotalReturnPrice());
                returnTransactionRepository.addReturn(currentReturnTransaction, returnTransactionRepository.getNewId());
                  if(db.insertReturnTransaction(currentReturnTransaction , st.getTicketNumber()))
                      return true;
                  else{
                      returnTransactionRepository.removeReturnTransaction(returnId);
                      return false;
                  }*/
            }
        } else {
            currentReturnTransaction = null;
            return false;
        }
    }


    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        boolean up = true;

        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if ((loggedUser.getRole() == null) || (loggedUser.getRole().isEmpty()) ||
                !(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager"))) {
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        } else if (returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid returnID. Please, try again. ");
        }
        ReturnTransactionClass rt = returnTransactionRepository.getReturnTransactionById(returnId);
        if (rt == null)
            return false;
        SaleTransactionClass st = rt.getSaleTransaction();

        if (st != null) {
            if (!st.isPaid()) return false;
            if (db.deleteReturnTransaction(rt)) {
                for (TicketEntry t : returnTransactionRepository.getReturnTransactionById(returnId).getEntriesList()) {
                    Integer finalquantity = products.getProductTypeByBarCode(t.getBarCode()).getQuantity() - t.getAmount();
                    db.productsUpdateQuantity(products.getProductTypeByBarCode(t.getBarCode()).getId(), finalquantity);
                    TicketEntry respectiveTicket = st.getEntryByBarCode(t.getBarCode());
                    respectiveTicket.setAmount(respectiveTicket.getAmount() + t.getAmount());
                }

                //   rt.updateSaleEntries(false);
                st.setPrice(st.calculatePrice());
                //   rt.updateProductQuantities(products, false);

            /*    for(TicketEntry t : rt.getEntriesList()){
                    if(!db.productsUpdateQuantity(products.getProductTypeByBarCode(t.getBarCode()).getId(), products.getProductTypeByBarCode(t.getBarCode()).getQuantity() + t.getAmount()))
                        up = false;
                }*/
                returnTransactionRepository.removeReturnTransaction(returnId);
                return up;
            } else return false;
        } else
            return false;
    }


    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (cash <= 0.0)
            throw new InvalidPaymentException("Invalid cash. Please, give enough money. ");

        if (ticketNumber == null || ticketNumber <= 0)
            throw new InvalidTransactionIdException("Invalid transactionID. Please, try again. ");

        SaleTransactionClass sale;
        if (openTransaction)
            sale = currentTransaction;
        else
            sale = saleTransactions.getTransaction(ticketNumber);

        if ((sale == null) || (cash < sale.getPrice()) || (sale.isPaid()))
            return -1;

        sale.setPaymentType("Cash");
        if (!db.saleIsPaid(sale)) {
            return -1;
        }
        double change = cash - sale.getPrice();
        if (recordBalanceUpdate(sale.getPrice())) {
            sale.setPaid(true);
            return change;
        } else return -1;
    }


    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("Cashier") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("tYou are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        if (creditCard == null || creditCard.equals("") || !this.validCreditCard(creditCard))
            throw new InvalidCreditCardException("Invalid credit card. Please, try again. ");

        if (ticketNumber == null || ticketNumber <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number. Please, try again. ");

        SaleTransactionClass sale;
        if (openTransaction)
            sale = currentTransaction;
        else
            sale = saleTransactions.getTransaction(ticketNumber);

        if ((sale == null) || (sale.isPaid()))
            return false;

        CreditCard cc = this.creditCards.getCreditCard(creditCard);
        if (cc == null || cc.getCredit() < sale.getPrice())
            return false;

        sale.setPaymentType("Credit Card");
        if (!db.saleIsPaid(sale)) {
            return false;
        }
        sale.setPaid(true);

        return recordBalanceUpdate(sale.getPrice());
    }


    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("ERROR: there is no logged user");
        String role = loggedUser.getRole();

        if (!(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager")))
            throw new UnauthorizedException("this role is not valid");

        if (returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();
        ReturnTransactionClass rt = returnTransactionRepository.getReturnTransactionById(returnId);

        if (rt == null || !rt.getSaleTransaction().isPaid() || rt.getSaleTransaction() == null) {
            return -1;
        }
        if (this.recordBalanceUpdate(-rt.getPrice())) {
            rt.setSaleTransaction(null);
            return rt.getPrice();
        } else
            return -1;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("ERROR: there is no logged user");
        String role = loggedUser.getRole();

        if (!(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager")))
            throw new UnauthorizedException("this role is not valid");

        if (creditCard == null || creditCard.equals("") || !this.validCreditCard(creditCard))
            throw new InvalidCreditCardException();
        CreditCard cc = creditCards.getCreditCard(creditCard);

        if (returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();

        if (cc == null)
            return -1;

        ReturnTransactionClass rt = returnTransactionRepository.getReturnTransactionById(returnId);

        if (rt == null || !rt.getSaleTransaction().isPaid() || rt.getSaleTransaction() == null) {
            return -1;
        }

        if (this.recordBalanceUpdate(-rt.getPrice())) {
            cc.setCredit(rt.getPrice());
            rt.setSaleTransaction(null);
            return rt.getPrice();
        } else
            return -1;
    }


    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        if (!(loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");


        LocalDate data = LocalDate.now();
        int numBalanceOperations = accountbook.getNewId();
        BalanceOperationClass bo;
        if (toBeAdded + accountbook.currentBalance < 0)
            return false;

        if (toBeAdded >= 0)
            bo = new BalanceOperationClass(numBalanceOperations, data, toBeAdded, "credit");
        else
            bo = new BalanceOperationClass(numBalanceOperations, data, toBeAdded, "debit");

        if (db.addBalanceOperation(bo)) {
            accountbook.addBalanceOperation(numBalanceOperations, bo);
            return true;
        } else return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        String role = loggedUser.getRole();
        if (!(role.equals("ShopManager") || role.equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");

        List<BalanceOperationClass> bo = new ArrayList<>();
        LocalDate tmp;
        if (from == null) from = LocalDate.MIN;
        if (to == null) to = LocalDate.now();

        if (to.isBefore(from)) {
            tmp = to;
            to = from;
            from = tmp;
        }
        for (BalanceOperationClass b : accountbook.getBalanceOperationList())
            if ((b.getDate().isBefore(to) || b.getDate().isEqual(to)) && (b.getDate().isAfter(from)))
                bo.add(b);

        return new ArrayList<>(bo);
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        if (loggedUser == null)
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        String role = loggedUser.getRole();
        if (!(role.equals("Cashier") || role.equals("ShopManager") || role.equals("Administrator")))
            throw new UnauthorizedException("You are not authorized. Please, login with an Administrator, ShopManager or Cashier account.");
        return accountbook.getCurrentBalance();
    }


}
