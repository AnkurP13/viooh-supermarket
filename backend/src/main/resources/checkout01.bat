curl -X POST "http://localhost:8080/supermarket/itemcheckout" ^
  -H "Accept: application/json" ^
  -H "Content-Type: multipart/form-data" ^
  -F "itemsFile=@C:/Users/n507822/WorkspaceVS/viooh-supermarket/backend/src/main/resources/checkout01/item.csv" ^
  -F "rulesFile=@C:/Users/n507822/WorkspaceVS/viooh-supermarket/backend/src/main/resources/checkout01/rule.csv" ^
  -o output.json