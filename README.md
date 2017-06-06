# StudentDBMS_Server
Server program for student dbms

How to setup:
 1. Create a folder "typhserver".
 2. CD to typhserver.
 3. Create another folder "sys".
 4. Place the following files inside "sys" folder:
    a. server.pem -> PEM FILE.
    b. typh.cfg   -> The configuration file.
    c. typh.ks    -> The keystore file.
 5. Place "run.bat" file in "typhserver" folder but outside "sys".
 6. Double Click "run.bat" to run program.
 
 
typh.cfg:
  This file is used incase a separate configuration file is not provided from command line.
  You may need to change the path of PEM file inside the configuration file.
  PEM file's default location is:
    C:/Program Files/MongoDB/Server/3.4/bin/cert/typh.pem
    
  Only one PEM file is used for both client and server (for simplicity only, the actual implementation of ssl is different) for this application. Use the same PEM file for:
    1. Client
    2. Server
    3. MongoDB ( typh.cfg )
    
    
