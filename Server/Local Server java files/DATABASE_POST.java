package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DATABASE_POST 
{
	private final static String USERNAME = "dbu309sd4";
	private final static String PASSWORD = "xeft3GXR";
	private final static String URL = "jdbc:mysql://mysql.cs.iastate.edu:3306/db309sd4";
	private final static int QRCODE_SIZE = 10000;
	
	/**
	 * 
	 * @param str - The string to have all invalid characters removed
	 * @return The string without any invalid characters
	 */
	//Scrubs the string for any invalid characters
	private static String Scrubber(String str)
	{
		//System.out.println(str);
		str.replaceAll("'", "\\\'");
		//System.out.println(str);
		return str;
	}
	
			/**
			 * 
			 * @param order_ID The order ID to be updated
			 * @param rest_ID The restaurant the order ID corresponds to
			 * @param Status - The status to be updated
			 * @return True if success, false otherwise
			 */
			public static boolean Update_Order_Status(String order_ID,String rest_ID, String Status)
			{
			
				try
				 {  		
				        Class.forName("com.mysql.jdbc.Driver");
				        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
				        
			            String query = "UPDATE db309sd4.Order ";
			            query += " SET ";
			            query += " Order_Status = '" + Status + "' ";
			            
			            if(Status.equalsIgnoreCase("Completed"))
			            {
				        		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				        		LocalDateTime now = LocalDateTime.now();
				        		
				        		query += " , Order_Date_Completed = '" + dtf.format(now) + "' ";
			            }
			            
			            query += " Where Order_ID = '" +  order_ID +"' AND ";
			            query += " Rest_ID = '" +  rest_ID +"' ";
			       
			            
			           
			            System.out.println(query);
			            Statement stmt=con.createStatement();
			            stmt.executeUpdate(query);
			       
			            con.close();
			            return true;
			        }
			      catch(Exception e)
			      {
			           e.printStackTrace();
			           return false;
			      }
			}
	
			/**
			 * Adds an order to the database. 
			 * 
			 * @param Order_ID The new Order_ID (Generated by the Main Controller, Usually )
			 * @param Rest_ID The Restaurant ID for the order
			 * @param User_ID The user who ordered  the ID
			 * @param Food The Food ID (One per Order) 
			 * @param Order_Data_Submitted Time order was submitted 
			 * @param Order_Data_Pick_Up Time order is to be picked up. 
			 * @param Order_Data_Completed Time order was completed
			 * @param Comments Comments for the order
			 * @param Quantity Quantity of Food
			 * @param QR_CODE QR Code of the order
			 * 
			 * @return True if success, false otherwise
			 */
			public static boolean Add_Order(int Order_ID, String Rest_ID, String User_ID, String Food,  String Order_Data_Submitted, String Order_Data_Pick_Up, String Order_Data_Completed, String Comments, String Quantity, String QR_CODE)
			{ 
				 try
				 {  		
				        Class.forName("com.mysql.jdbc.Driver");
				        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
			             
				        
			            String query = "INSERT INTO db309sd4.Order ";
			            query += "( ";
			            query += "Order_ID,";
			            query += "Rest_ID,";
			            query += "User_ID,";
			            query += "Food_ID,";
			            query += "Order_Date_Submitted,";
			            query += "Order_Date_Pick_Up,";
			            query += "Comments,";
			            query += "Quantity,";
			            query += "Order_Status,";
			            query += "Order_Confirmation_Code";
			            query += ") ";
			            
			            query += " VALUES ( ";
			            query += "'" + Order_ID + "', ";
			            query += "'" + Rest_ID + "', ";
			            query += "'" + User_ID + "', ";
			            query += "'" + Food + "', ";
			            query += "'" + Order_Data_Submitted + "', ";
			            query += "'" + Order_Data_Pick_Up + "', ";
			            query += "'" + Comments + "',";
			            query += "'" + Quantity + "',";
			            query += "'" + "Pending" + "',";
			            query += "'" + QR_CODE + "')";
			            
			           
			            System.out.println(query);
			            Statement stmt=con.createStatement();
			            stmt.executeUpdate(query);
			       
			            con.close();
			            return true;
			        }
			      catch(Exception e)
			      {
			           e.printStackTrace();
			           return false;
			      }

			}
	
		/**
		 * Adds a Food to the base. 
		 * @param Rest_ID  Restaurant Food is associated with
		 * @param Food_Name Name of Food. 
		 * @param Food_Price Price of Food. 
		 * @param Food_Desc Food Description
		 * @param Menu_ID Menu designation (Always 1 if restaurant only has 1 menu)
		 * @param Food_Tags_Main Tag for food
		 * @param Food_Tags_Secondary Secondary tag for food
		 * 
		 * @return True if success, false otherwise
		 */
		public static boolean Add_Food(String Rest_ID, String Food_Name, String Food_Price,  String Food_Desc, String Menu_ID,String Food_Tags_Main, String Food_Tags_Secondary, String Food_ID)
		{ 
			 try
			 {  		
			        Class.forName("com.mysql.jdbc.Driver");
			        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
		           
		            String query = "INSERT INTO Food ";
		            query += " VALUES ( ";
		            query += "'" + Rest_ID + "', ";
		            query += "'" + Food_Name + "', ";
		            query += "'" + Food_Price + "', ";
		            query += "'" + Food_Desc + "', ";
		            query += "'" + Menu_ID + "', ";
		            query += "'" + Food_Tags_Main + "', ";
		            query += "'" + Food_Tags_Secondary + "', ";
		            query += "'" + Food_ID + "'); ";
		            
		           
		            System.out.println(query);
		            Statement stmt=con.createStatement();
		            stmt.executeUpdate(query);
		            
		        
		            con.close();
		            return true;
		        }
		      catch(Exception e)
		      {
		           e.printStackTrace();
		           return false;
		      }

		}
	
	
	/**
	 * Adds a user to the database
	 * @param User_Type      User Type, Admin/User/Vendor
	 * @param First_Name
	 * @param Last_Name
	 * @param User_Address - Can be null. 
	 * @param User_Birthday
	 * @param User_Email
	 * @param User_Password
	 * 
	 * @return True if success, false otherwise
	 */
	public static boolean Add_User( String User_Type, String First_Name,  String Last_Name, String User_Address,String User_Birthday, String User_Email, String User_Password)
	{ 
		 try
		 {  		
		        Class.forName("com.mysql.jdbc.Driver");
		        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
	           
		        


	            String query = "INSERT INTO User ( User_Type ,  First_Name ,  Last_Name ,  User_Address ,  User_Birthdate ,  User_Email  ,  User_Password )";
	            query += " VALUES ( ";
	            query += "'" + User_Type + "', ";
	            query += "'" + First_Name + "', ";
	            query += "'" + Last_Name + "', ";
	            query += "'" + User_Address + "', ";
	            query += "'" + User_Birthday  + "',";
	            query += "'" + User_Email + "', ";
	            query += "'" + User_Password + "'); ";
	            
	           
	            System.out.println(query);
	            Statement stmt=con.createStatement();
	            stmt.executeUpdate(query);
	       
	            con.close();
	            return true;
	        }
	      catch(Exception e)
	      {
	           e.printStackTrace();
	           return false;
	      }

	}
	
	
	
	/**
	 * Adds a Restaurant to the database
	 * @param Rest_Name
	 * @param Rest_Coordinate_X  Latitude 
	 * @param Rest_Coordinate_Y  Longitude
	 * @param Rest_Address
	 * @param Rest_Rating Restaurant rating
	 * 
	 * @return True if success, false otherwise
	 *
	 * //Written by Adam de Gala. Feel free to yell at me. 
	 */
	public static boolean Add_Restaurant(String Rest_Name, String Rest_Coordinate_X,  String Rest_Coordinate_Y, String Rest_Address,String Rest_Rating)
	{ 
		 try
		 {  		
		        Class.forName("com.mysql.jdbc.Driver");
		        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
	           
	            String query = "INSERT INTO Restaurant ";
	            query += " VALUES ( ";
	            query += "'" + Scrubber(Rest_Name) + "', ";
	            query += "'" + Rest_Coordinate_X + "', ";
	            query += "'" + Rest_Coordinate_Y + "', ";
	            query += "'" + Rest_Address + "', ";
	            query += "'" + Rest_Rating + "'); ";
	            
	           
	            System.out.println(query);
	            Statement stmt=con.createStatement();
	            stmt.executeUpdate(query);
	            

	            con.close();
	            return true;
	        }
	      catch(Exception e)
	      {
	           e.printStackTrace();
	           return false;
	      }

	}
	
		/**
		 * Adds a RSA public key for the world to see
		 * @param n   The public Key
		 * @param encryptionExponet The public exponent Key
		 * 
		 * @return True if success, false otherwise
		 */
		public static boolean Add_New_RSA_Key(int n, int encryptionExponet)
		{ 
			 try
			 {  		
			        Class.forName("com.mysql.jdbc.Driver");
			        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
		           
			        


		            String query = "INSERT INTO Server_Keys ( Public_Key ,  Encyption_Exponet)";
		            query += " VALUES ( ";
		            query += "'" + n + "', ";
		            query += "'" + encryptionExponet + "'); ";
		            
		           
		            System.out.println(query);
		            Statement stmt=con.createStatement();
		            stmt.executeUpdate(query);
		       
		            con.close();
		            return true;
		        }
		      catch(Exception e)
		      {
		           e.printStackTrace();
		           return false;
		      }

		}
		
		/**
		 * Adds a ESA Key Unique to a user. 
		 * @param User_ID User ID this key corresponds too
		 * @param ESA_Key The Key
		 * @return True if success, false otherwise
		 */
		public static boolean Add_New_ESA_Key(String User_ID, String ESA_Key)
		{ 
			 try
			 {  		
			        Class.forName("com.mysql.jdbc.Driver");
			        Connection con= DriverManager.getConnection(URL,USERNAME, PASSWORD);
		           
			        //Delete the old ESA Key if it exist
			        String query = "DELETE FROM USER_KEYS WHERE USER_ID = \"" + User_ID + "\"";
		            Statement stmt=con.createStatement();
		            stmt.executeUpdate(query);
			        

		            query = "INSERT INTO User_Keys ";
		            query += " VALUES ( ";
		            query += "'" + User_ID + "', ";
		            query += "'" + ESA_Key + "'); ";
		            
		           
		            System.out.println(query);
		            stmt=con.createStatement();
		            stmt.executeUpdate(query);
		       
		            con.close();
		            return true;
		        }
		      catch(Exception e)
		      {
		           e.printStackTrace();
		           return false;
		      }

		}
}
