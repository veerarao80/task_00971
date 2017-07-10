# task_00971

run the server by running the following command in path c:\...\ebi\pom.xml path.

mvn spring-boot:run 



http://localhost:8080/accessionNumberRanges in Explorer text box or pressing the green button to view all the acession groups / categories.

![main_screen](https://github.com/veerarao80/task_00971/blob/master/images/main_screen.png)

Press Green button in the above to get the accession groups as seen in the screen below.

![first screen accession Groups](https://github.com/veerarao80/task_00971/blob/master/images/accessionGroups.png)

post / delete can be done in the following format.

{
  "suffixGroups":["A0001","A0004","A0005"]
}

or curl -X POST 
     -H 'application/hal+json;charset=UTF-8' 
     -d '{ "suffixGroups":["A0001","A0004","A0005"] }'


![post screen](https://github.com/veerarao80/task_00971/blob/master/images/post_or_delete.png)

json accession groups can be accessed from http://localhost:8080/accessionGroups

![json result for accession groups](https://github.com/veerarao80/task_00971/blob/master/images/accessionGroup_results.png)


