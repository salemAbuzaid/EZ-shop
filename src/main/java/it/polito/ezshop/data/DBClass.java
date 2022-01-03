package it.polito.ezshop.data;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DBClass {

    /* METODI IMPORT -> DA DB A MAPPE */

    public Orders importOrders() {
        Orders orders = new Orders();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String impPr = "SELECT * FROM ORDERS";
            PreparedStatement Stmt = connection.prepareStatement(impPr);
            ResultSet rs = Stmt.executeQuery();

            while (rs.next()) {
                OrderClass order = new OrderClass();

                order.setOrderId(rs.getInt("ID"));
                order.setPricePerUnit(rs.getDouble("pricePerUnit"));
                order.setQuantity(rs.getInt("quantity"));
                order.setStatus(rs.getString("status"));
                order.setBalanceId(rs.getInt("balanceID"));
                order.setProductCode(rs.getString("productCode"));
                orders.addOrder(order);
            }
            rs.close();
            connection.close();
            orders.setNewId();
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Users importUsers() {
        Users users = new Users();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String impUs = "SELECT * FROM USERS";
            PreparedStatement Stmt = connection.prepareStatement(impUs);
            ResultSet rs = Stmt.executeQuery();

            while (rs.next()) {
                UserClass user = new UserClass();
                user.setId(rs.getInt("ID"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.addUser(user);
            }
            rs.close();
            connection.close();
            users.setNewId();
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Products importProducts() {
        Products products = new Products();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String impPr = "SELECT * FROM PRODUCTS";
            PreparedStatement Stmt = connection.prepareStatement(impPr);
            ResultSet rs = Stmt.executeQuery();

            while (rs.next()) {
                ProductTypeClass product = new ProductTypeClass();
                product.setId(rs.getInt("ID"));
                product.setBarCode(rs.getString("barCode"));
                product.setProductDescription(rs.getString("productDescription"));
                product.setQuantity(rs.getInt("quantity"));
                product.setPricePerUnit(rs.getDouble("PPU"));
                product.setDiscountRate(rs.getDouble("discountRate"));
                product.setNote(rs.getString("note"));
                product.setLocation(rs.getString("location"));
                products.addProduct(product);
            }
            rs.close();
            connection.close();
            products.giveNewId();
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProductsRFID importProductsRFID() {
        ProductsRFID products = new ProductsRFID();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String impPrRFID = "SELECT * FROM PRODUCTSRFID";
            PreparedStatement Stmt = connection.prepareStatement(impPrRFID);
            ResultSet rs = Stmt.executeQuery();

            while (rs.next()) {
                ProductRFID product = new ProductRFID();
                product.setRFID(rs.getString("RFID"));
                product.setBarCode(rs.getString("barCode"));
                product.setSold(rs.getBoolean("sold"));
                products.addProductRFID(product.getRFID(), product);
            }
            rs.close();
            connection.close();
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SaleTransactions importTransactions() {
        SaleTransactions saletransactions = new SaleTransactions();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String impsals = "SELECT * FROM SALETRANSACTIONS";
            PreparedStatement Stmt1 = connection.prepareStatement(impsals);
            ResultSet rs = Stmt1.executeQuery();

            while (rs.next()) {
                SaleTransactionClass st = new SaleTransactionClass();
                st.setTicketNumber(rs.getInt("ID"));
                st.setPrice(rs.getDouble("price"));
                st.setPaymentType(rs.getString("paymentType"));
                st.setDiscountRate(rs.getDouble("discountRate"));
                String impEntr = "SELECT * FROM TICKETENTRIESINSALETRANSACTIONS WHERE sale_ID = ? ";
                PreparedStatement stmt2 = connection.prepareStatement(impEntr);
                stmt2.setInt(1 ,st.getTicketNumber());
                ResultSet rsEntr = stmt2.executeQuery();
                while(rsEntr.next()){
                    TicketEntryClass t = new TicketEntryClass();
                    t.setBarCode(rsEntr.getString("barCode"));
                    t.setProductDescription(rsEntr.getString("productDescription"));
                    t.setAmount( rsEntr.getInt("amount"));
                    t.setPricePerUnit(rsEntr.getDouble("ppu"));
                    t.setDiscountRate(rsEntr.getDouble("discountRate"));
                    st.addNewTicketEntry(t);
                }
                saletransactions.addTransaction(st);
            }
            rs.close();
            connection.close();
            saletransactions.setNewId();
            return saletransactions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ReturnTransactionRepository importReturnTransactions() {
        ReturnTransactionRepository rtp = new ReturnTransactionRepository();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String imprt = "SELECT * FROM RETURNTRANSACTIONS";
            PreparedStatement Stmt = connection.prepareStatement(imprt);
            ResultSet rs = Stmt.executeQuery();
            int id;
            while (rs.next()) {
                ReturnTransactionClass rt = new ReturnTransactionClass();
                id =rs.getInt("ID");
                rt.setPrice(rs.getDouble("price"));
                String impEntr = "SELECT * FROM TICKETENTRIESINRETURN WHERE returnID = ? ";
                PreparedStatement stmt2 = connection.prepareStatement(impEntr);
                stmt2.setInt(1 ,rt.getId());
                ResultSet rsRtEntr = stmt2.executeQuery();
                while(rsRtEntr.next()){
                    TicketEntryClass t = new TicketEntryClass();
                    t.setBarCode(rsRtEntr.getString("bareCode"));
                    t.setProductDescription(rsRtEntr.getString("productDescription"));
                    t.setAmount( rsRtEntr.getInt("amount"));
                    t.setPricePerUnit(rsRtEntr.getDouble("ppu"));
                    t.setDiscountRate(rsRtEntr.getDouble("discountRate"));
                    rt.addEntry(t.getBarCode(),t);
                }
                rtp.addReturn(rt,id);

            }
            rs.close();
            connection.close();
            rtp.setNewId();
            return rtp;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Customers importCustomers() {
        Customers customers = new Customers();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "SELECT * FROM CUSTOMERS";
            PreparedStatement Stmt = connection.prepareStatement(query);
            ResultSet rs = Stmt.executeQuery();

            while (rs.next()) {
                CustomerClass customer = new CustomerClass();
                customer.setId(rs.getInt("ID"));
                customer.setCustomerName(rs.getString("name"));
                customer.setCustomerCard(rs.getString("card"));
                customer.setPoints(rs.getInt("points"));
                customers.addCustomer(customer.getId(),customer);
            }
            rs.close();
            connection.close();
            customers.setNewId();
            return customers;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountBook importAccounting() {
        AccountBook accountBook = new AccountBook();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String currBalance = "SELECT * FROM CURRENTBALANCE";
            PreparedStatement Stmt = connection.prepareStatement(currBalance);
            ResultSet rs = Stmt.executeQuery();
            while (rs.next())
                accountBook.setCurrentBalance(rs.getDouble("currentbalance"));
            rs.close();

            String balanceOps = "SELECT * FROM ACCOUNTBOOK";
            PreparedStatement Stmt2 = connection.prepareStatement(balanceOps);
            ResultSet rs2 = Stmt2.executeQuery();
            while (rs2.next()) {
                BalanceOperationClass bo = new BalanceOperationClass();
                bo.setBalanceId(rs2.getInt("id"));
                bo.setDate(LocalDate.parse(rs2.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                bo.setType(rs2.getString("type"));
                bo.setMoney(rs2.getDouble("money"));
                accountBook.addBalanceOperation(bo.getBalanceId(), bo);
            }
            rs2.close();
            connection.close();
            accountBook.setNewId();
            return accountBook;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* METODI DELETE -> RESET, SVUOTA LE TABELLE DEL DB */

    public boolean deleteOrders() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteAllOrders = "DELETE FROM ORDERS";
            PreparedStatement Stmt = connection.prepareStatement(deleteAllOrders);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProducts() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteAllProducts = "DELETE FROM PRODUCTS";
            PreparedStatement Stmt = connection.prepareStatement(deleteAllProducts);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProductsRFID() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteAllProductsRFID = "DELETE FROM PRODUCTSRFID";
            PreparedStatement Stmt = connection.prepareStatement(deleteAllProductsRFID);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUsers() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteAllUsers = "DELETE FROM USERS";
            PreparedStatement Stmt = connection.prepareStatement(deleteAllUsers);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSaleTransactions (){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteTransaction = "DELETE FROM SALETRANSACTIONS ";
            PreparedStatement dltTransatStat= connection.prepareStatement(deleteTransaction);

            dltTransatStat.executeUpdate();
            dltTransatStat.close();
            String deleteEntries = "DELETE FROM TICKETENTRIESINSALETRANSACTIONS";
            PreparedStatement dltEntriesStat = connection.prepareStatement(deleteEntries);
            dltEntriesStat.executeUpdate();
            dltEntriesStat.close();
            connection.close();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean deleteReturnTransactions(){
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")){
            String deleteRetTransaction = " DELETE FROM RETURNTRANSACTIONS";
            PreparedStatement dltRetTrans = connection.prepareStatement(deleteRetTransaction);
            dltRetTrans.executeUpdate();
            dltRetTrans.close();

            String deleteEntries = "DELETE FROM TICKETENTRIESINRETURN";
            PreparedStatement dltEntriesStat = connection.prepareStatement(deleteEntries);
            dltEntriesStat.executeUpdate();
            dltEntriesStat.close();
            connection.close();
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomers(){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "DELETE FROM CUSTOMERS";
            PreparedStatement Stmt = connection.prepareStatement(query);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteAccounting() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String delAcBook = "DELETE FROM ACCOUNTBOOK";
            String delCurBal = "UPDATE CURRENTBALANCE SET CURRENTBALANCE = 0";
            PreparedStatement Stmt1 = connection.prepareStatement(delAcBook);
            Stmt1.executeUpdate();
            PreparedStatement Stmt2 = connection.prepareStatement(delCurBal);
            Stmt2.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* METODI CUSTOMERS */

    public boolean addCustomer(CustomerClass newCustomer) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "INSERT INTO CUSTOMERS(ID, name, card, points) VALUES (?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setInt(1, newCustomer.getId());
            stat.setString(2, newCustomer.getCustomerName());
            stat.setString(3, newCustomer.getCustomerCard());
            stat.setDouble(4, newCustomer.getPoints());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(Integer id){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "DELETE FROM CUSTOMERS WHERE ID = ?";
            PreparedStatement Stmt = connection.prepareStatement(query);
            Stmt.setInt(1,id);
            Stmt.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerName(Integer id, String newName){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "UPDATE CUSTOMERS SET NAME = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setString(1, newName);
            stat.setInt(2, id);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerCard(Integer id, String newCard){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "UPDATE CUSTOMERS SET CARD = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setString(1, newCard);
            stat.setInt(2, id);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attachCard(Integer id, String card){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "UPDATE CUSTOMERS SET CARD = ?, POINTS = 0 WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setString(1, card);
            stat.setInt(2, id);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePoints(String card, Integer points){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String query = "UPDATE CUSTOMERS SET POINTS = POINTS + ? WHERE CARD = ?";
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setInt(1, points);
            stat.setString(2, card);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CustomerClass getCustomer(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getCustomer = "SELECT  * FROM CUSTOMERS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getCustomer);
            stat.setString(1, id.toString());
            ResultSet rs = stat.executeQuery();

            CustomerClass c = new CustomerClass(rs.getInt("ID"),
                    rs.getString("name"),
                    rs.getString("card"),
                    rs.getInt("points"));
            stat.close();
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* METODI ORDERS */

    public boolean addOrder(OrderClass newOrder) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addOrder = "INSERT INTO ORDERS(ID, pricePerUnit, quantity, status, balanceID, productCode) VALUES (?,?,?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(addOrder);
            stat.setString(1, newOrder.getOrderId().toString());
            stat.setString(2, Double.toString( newOrder.getPricePerUnit() ) );
            stat.setString(3, Integer.toString(newOrder.getQuantity() ) );
            stat.setString(4, newOrder.getStatus() );
            stat.setString(5, Integer.toString( newOrder.getBalanceId() ) );
            stat.setString(6, newOrder.getProductCode() );

            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeOrder(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteOrder = "DELETE FROM ORDERS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(deleteOrder);
            stat.setString(1, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public OrderClass getOrder(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getOrder = "SELECT  * FROM ORDERS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getOrder);
            stat.setString(1, id.toString());
            ResultSet rs = stat.executeQuery();

            OrderClass order = new OrderClass();
            order.setOrderId(rs.getInt("ID"));
            order.setPricePerUnit(rs.getDouble("pricePerUnit"));
            order.setQuantity(rs.getInt("quantity"));
            order.setStatus(rs.getString("status"));
            order.setBalanceId(rs.getInt("balanceID"));
            order.setProductCode(rs.getString("productCode"));
            stat.close();
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean orderUpdatePricePerUnit(Integer id, Double ppu) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getOrder = "UPDATE ORDERS SET pricePerUnit = ? WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getOrder);
            stat.setString(1, Double.toString(ppu));
            stat.setString(2, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean orderUpdateQuantity(Integer id, Integer qty) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getOrder = "UPDATE ORDERS SET quantity = ? WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getOrder);
            stat.setString(1, Integer.toString(qty));
            stat.setString(2, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean orderUpdateStatus(Integer id, String status) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getOrder = "UPDATE ORDERS SET status = ? WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getOrder);
            stat.setString(1, status );
            stat.setString(2, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean orderUpdateBalanceID(Integer id, Integer balanceID) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getOrder = "UPDATE ORDERS SET balanceID = ? WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getOrder);
            stat.setString(1, Integer.toString(balanceID));
            stat.setString(2, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* METODI USERS */

    public boolean addUser(User new_user) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addUser = "INSERT INTO USERS(id, username, password, role) VALUES (?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(addUser);
            stat.setString(1, new_user.getId().toString());
            stat.setString(2, new_user.getUsername());
            stat.setString(3, new_user.getPassword());
            stat.setString(4, new_user.getRole());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeUser(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deletetUser = "DELETE FROM USERS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(deletetUser);
            stat.setString(1, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean updateUserRights(Integer id, String role) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updateUser = "UPDATE USERS SET ROLE = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(updateUser);
            stat.setString(1, role);
            stat.setString(2, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getUser = "SELECT  * FROM USERS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getUser);
            stat.setInt(1, id);
            ResultSet rs = stat.executeQuery();

            UserClass user = new UserClass();
            user.setId(rs.getInt("ID"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));

            stat.close();
            rs.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /* METODI PRODUCTS */

    public boolean addProductType(ProductTypeClass new_product) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addPr = "INSERT INTO PRODUCTS(ID, barCode, productDescription, PPU, quantity, discountRate, note, location) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(addPr);
            stat.setInt(1, new_product.getId());
            stat.setString(2, new_product.getBarCode());
            stat.setString(3, new_product.getProductDescription());
            stat.setDouble(4, new_product.getPricePerUnit());
            stat.setInt(5, new_product.getQuantity());
            stat.setDouble(6, new_product.getDiscountRate());
            stat.setString(7, new_product.getNote());
            stat.setString(8, new_product.getLocation());

            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProductType(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String removePr = "DELETE FROM PRODUCTS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(removePr);
            stat.setString(1, id.toString());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ProductTypeClass getProductType(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getProduct = "SELECT  * FROM PRODUCTS WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getProduct);
            stat.setString(1, id.toString());
            ResultSet rs = stat.executeQuery();

            ProductTypeClass product = new ProductTypeClass();
            product.setId(rs.getInt("ID"));
            product.setBarCode(rs.getString("barCode"));
            product.setProductDescription(rs.getString("productDescription"));
            product.setPricePerUnit(rs.getDouble("PPU"));
            product.setQuantity(rs.getInt("quantity"));
            product.setDiscountRate(rs.getDouble("discountRate"));
            product.setNote(rs.getString("note"));
            product.setLocation(rs.getString("location"));

            stat.close();
            return product;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean updateProductType(Integer id, String newDescription, String newCode, double newPrice, String newNote) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updatePr = "UPDATE PRODUCTS SET BARCODE = ?, PRODUCTDESCRIPTION = ?, PPU = ?, NOTE = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(updatePr);
            stat.setString(1, newCode);
            stat.setString(2, newDescription);
            stat.setDouble(3, newPrice);
            stat.setString(4, newNote);
            stat.setInt(5, id);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean productsUpdateQuantity(Integer productId, int finalQuantity){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updatePr = "UPDATE PRODUCTS SET QUANTITY = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(updatePr);
            stat.setInt(1, finalQuantity);
            stat.setInt(2, productId);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean productsUpdatePosition(Integer productId, String newPos){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updatePr = "UPDATE PRODUCTS SET LOCATION = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(updatePr);
            stat.setString(1, newPos);
            stat.setInt(2, productId);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* METODI PRODUCTS RFID */
    public boolean addProductRFID(ProductRFID new_product) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addPrRFID = "INSERT INTO PRODUCTSRFID(RFID, BARCODE, SOLD) VALUES (?,?,?)";
            PreparedStatement stat = connection.prepareStatement(addPrRFID);
            stat.setString(1, new_product.getRFID());
            stat.setString(2, new_product.getBarCode());
            stat.setBoolean(3, new_product.isSold());

            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSoldProductRFID(ProductRFID new_product) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String soldPrRFID = "UPDATE PRODUCTSRFID SET SOLD = ? WHERE RFID = ?";
            PreparedStatement stat = connection.prepareStatement(soldPrRFID);
            stat.setBoolean(1, new_product.isSold());
            stat.setString(2, new_product.getRFID());

            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    /* METODI SALE TRANSACTIONS */

    public boolean addSaleTransaction(SaleTransactionClass st) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addTransasction = "INSERT INTO SALETRANSACTIONS(ID ,price ,paymentType ,discountRate ) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(addTransasction);
            statement.setInt(1, st.getTicketNumber());
            statement.setDouble(2, st.getPrice());
            statement.setString(3, st.getPaymentType());
            statement.setDouble(4, st.getDiscountRate());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSaleEntries(List<TicketEntry> entries, int saleId) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String addEntry = "INSERT INTO TicketEntriesInSaleTransactions (barCode ,productDescription ,amount ,ppu ,discountRate ,sale_ID) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(addEntry);
            for (TicketEntry t : entries) {
                statement.setString(1, t.getBarCode());
                statement.setString(2, t.getProductDescription());
                statement.setInt(3, t.getAmount());
                statement.setDouble(4, t.getPricePerUnit());
                statement.setDouble(5, t.getDiscountRate());
                statement.setInt(6, saleId);
                statement.executeUpdate();
            }
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSaleTransaction(SaleTransactionClass st) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String deleteTransaction = "DELETE FROM SALETRANSACTIONS WHERE ID = ?";
            PreparedStatement dltTransatStat= connection.prepareStatement(deleteTransaction);
            dltTransatStat.setInt(1, st.getTicketNumber());
            dltTransatStat.executeUpdate();
            dltTransatStat.close();
            String deleteEntries = "DELETE FROM TicketEntriesInSaleTransactions WHERE sale_ID = ?";
            PreparedStatement dltEntriesStat = connection.prepareStatement(deleteEntries);
            dltEntriesStat.setInt(1, st.getTicketNumber());
            dltEntriesStat.executeUpdate();
            dltEntriesStat.close();
            connection.close();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saleIsPaid(SaleTransactionClass st){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updatePr = "UPDATE SALETRANSACTIONS SET PAYMENTTYPE = ? WHERE ID = ?";
            PreparedStatement stat = connection.prepareStatement(updatePr);
            stat.setString(1, st.getPaymentType());
            stat.setInt(2, st.getTicketNumber());
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* METODI RETURN TRANSACTION */

    public boolean insertReturnTransaction(ReturnTransactionClass rt , int returnID){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            SaleTransactionClass st = rt.getSaleTransaction();
            String addReturnTransaction = "INSERT INTO RETURNTRANSACTIONS (ID ,price ,saleID ) VALUES (?,?,?)";
            PreparedStatement addReturnStat = connection.prepareStatement(addReturnTransaction);
            addReturnStat.setInt(1,rt.getId());
            addReturnStat.setDouble(2,rt.getPrice());
            addReturnStat.setInt(3, st.getTicketNumber());
            addReturnStat.executeUpdate();
            addReturnStat.close();
            String insertEntries = "INSERT INTO TICKETENTRIESINRETURN (bareCode ,productDescription ,amount ,ppu, discountRate ,returnID) VALUES (?,?,?,?,?,?)";
            PreparedStatement insEntries = connection.prepareStatement(insertEntries);
            for (Map.Entry<String , TicketEntryClass> entry: rt.getEntries().entrySet()){
                insEntries.setString(1,entry.getValue().getBarCode());
                insEntries.setString(2,entry.getValue().getProductDescription());
                insEntries.setInt(3,entry.getValue().getAmount());
                insEntries.setDouble(4,entry.getValue().getPricePerUnit());
                insEntries.setDouble(5,entry.getValue().getDiscountRate());
                insEntries.setInt(6,returnID);
                insEntries.executeUpdate();
            }
            insEntries.close();
            connection.close();

            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReturnTransaction(ReturnTransactionClass rt){
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")){
            int returnID = rt.getId();
            String delRetTr = "DELETE FROM RETURNTRANSACTIONS WHERE ID = ?";
            PreparedStatement stat1 = connection.prepareStatement(delRetTr);
            stat1.setInt(1, returnID);
            String delEntries = "DELETE FROM TICKETENTRIESINRETURN WHERE returnID = ?";
            PreparedStatement stat2 = connection.prepareStatement(delEntries);
            stat2.setInt(1, returnID);
            stat2.executeUpdate();
            //call to a method that should update SaleTransactions
            updateSaleTransactions(rt.getSaleTransaction());
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateSaleTransactions (SaleTransactionClass st ){
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            Integer saleID = st.getTicketNumber();
            String updatePrice = "UPDATE SALETRANSACTIONS SET PRICE = ? WHERE ID = ?";
            PreparedStatement stat1 = connection.prepareStatement(updatePrice);
            stat1.setInt(1, saleID);
            stat1.executeUpdate();
            stat1.close();


            String insetEntries = "INSERT OR REPLACE INTO TicketEntriesInSaleTransactions (barCode ,productDescription ,amount ,ppu, discountRate ,sale_ID) VALUES (?,?,?,?,?,?)";
            PreparedStatement stat3 = connection.prepareStatement(insetEntries);

            for (TicketEntry t : st.getEntries()) {
                stat3.setString(1, t.getBarCode());
                stat3.setString(2, t.getProductDescription());
                stat3.setDouble(3, t.getAmount());
                stat3.setDouble(4, t.getPricePerUnit());
                stat3.setDouble(5, t.getDiscountRate());
                stat3.setInt(6, saleID);
                stat3.executeUpdate();
            }

            stat3.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*  METODI ACCOUNT BOOK */

    public boolean addBalanceOperation(BalanceOperationClass newBO) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String insBalOp = "INSERT INTO ACCOUNTBOOK(ID, date, money, type) VALUES (?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(insBalOp);
            stat.setInt(1, newBO.getBalanceId());
            stat.setString(2,newBO.getDate().toString());
            stat.setDouble(3, newBO.getMoney());
            stat.setString(4, newBO.getType());
            stat.executeUpdate();
            stat.close();
            updateBalance(newBO.getMoney());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalance(Double amount) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String updCurBal = "UPDATE CURRENTBALANCE SET CURRENTBALANCE = CURRENTBALANCE + ? ";
            PreparedStatement stat = connection.prepareStatement(updCurBal);
            stat.setDouble(1, amount);
            stat.executeUpdate();
            stat.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public BalanceOperation getBalanceOperation(Integer id) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ezshop.sqlite")) {
            String getBalanceOp = "SELECT  * FROM ACCOUNTBOOK WHERE ID=?";
            PreparedStatement stat = connection.prepareStatement(getBalanceOp);
            stat.setInt(1, id);
            ResultSet rs = stat.executeQuery();

            BalanceOperation balance = new BalanceOperationClass();
            balance.setBalanceId((rs.getInt("ID")));
            balance.setDate(LocalDate.parse(rs.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            balance.setMoney(rs.getDouble("MONEY"));
            balance.setType(rs.getString("TYPE"));

            rs.close();
            stat.close();
            return balance;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
