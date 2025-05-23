$persons = @(
    '{"firstName":"John","lastName":"Boyd","address":"1509 Culver St","city":"Culver","zip":"97451","phone":"841-874-6512","email":"jaboyd@email.com"}',
    '{"firstName":"Jacob","lastName":"Boyd","address":"1509 Culver St","city":"Culver","zip":"97451","phone":"841-874-6513","email":"drk@email.com"}',
    '{"firstName":"Tenley","lastName":"Boyd","address":"1509 Culver St","city":"Culver","zip":"97451","phone":"841-874-6512","email":"tenz@email.com"}',
    '{"firstName":"Roger","lastName":"Boyd","address":"1509 Culver St","city":"Culver","zip":"97451","phone":"841-874-6512","email":"jaboyd@email.com"}',
    '{"firstName":"Felicia","lastName":"Boyd","address":"1509 Culver St","city":"Culver","zip":"97451","phone":"841-874-6544","email":"jaboyd@email.com"}',
    '{"firstName":"Jonanathan","lastName":"Marrack","address":"29 15th St","city":"Culver","zip":"97451","phone":"841-874-6513","email":"drk@email.com"}',
    '{"firstName":"Tessa","lastName":"Carman","address":"834 Binoc Ave","city":"Culver","zip":"97451","phone":"841-874-6512","email":"tenz@email.com"}',
    '{"firstName":"Peter","lastName":"Duncan","address":"644 Gershwin Cir","city":"Culver","zip":"97451","phone":"841-874-6512","email":"jaboyd@email.com"}',
    '{"firstName":"Foster","lastName":"Shepard","address":"748 Townings Dr","city":"Culver","zip":"97451","phone":"841-874-6544","email":"jaboyd@email.com"}',
    '{"firstName":"Tony","lastName":"Cooper","address":"112 Steppes Pl","city":"Culver","zip":"97451","phone":"841-874-6874","email":"tcoop@ymail.com"}',
    '{"firstName":"Lily","lastName":"Cooper","address":"489 Manchester St","city":"Culver","zip":"97451","phone":"841-874-9845","email":"lily@email.com"}',
    '{"firstName":"Sophia","lastName":"Zemicks","address":"892 Downing Ct","city":"Culver","zip":"97451","phone":"841-874-7878","email":"soph@email.com"}',
    '{"firstName":"Warren","lastName":"Zemicks","address":"892 Downing Ct","city":"Culver","zip":"97451","phone":"841-874-7512","email":"ward@email.com"}',
    '{"firstName":"Zach","lastName":"Zemicks","address":"892 Downing Ct","city":"Culver","zip":"97451","phone":"841-874-7512","email":"zarc@email.com"}',
    '{"firstName":"Reginold","lastName":"Walker","address":"908 73rd St","city":"Culver","zip":"97451","phone":"841-874-8547","email":"reg@email.com"}',
    '{"firstName":"Jamie","lastName":"Peters","address":"908 73rd St","city":"Culver","zip":"97451","phone":"841-874-7462","email":"jpeter@email.com"}',
    '{"firstName":"Ron","lastName":"Peters","address":"112 Steppes Pl","city":"Culver","zip":"97451","phone":"841-874-8888","email":"jpeter@email.com"}',
    '{"firstName":"Allison","lastName":"Boyd","address":"112 Steppes Pl","city":"Culver","zip":"97451","phone":"841-874-9888","email":"aly@imail.com"}',
    '{"firstName":"Brian","lastName":"Stelzer","address":"947 E. Rose Dr","city":"Culver","zip":"97451","phone":"841-874-7784","email":"bstel@email.com"}',
    '{"firstName":"Shawna","lastName":"Stelzer","address":"947 E. Rose Dr","city":"Culver","zip":"97451","phone":"841-874-7784","email":"ssanw@email.com"}',
    '{"firstName":"Kendrik","lastName":"Stelzer","address":"947 E. Rose Dr","city":"Culver","zip":"97451","phone":"841-874-7784","email":"bstel@email.com"}',
    '{"firstName":"Clive","lastName":"Ferguson","address":"748 Townings Dr","city":"Culver","zip":"97451","phone":"841-874-6741","email":"clivfd@ymail.com"}',
    '{"firstName":"Eric","lastName":"Cadigan","address":"951 LoneTree Rd","city":"Culver","zip":"97451","phone":"841-874-7458","email":"gramps@email.com"}'
)

foreach ($person in $persons) {
    Write-Host "Ajout de : $person"
    Invoke-WebRequest -Uri "http://localhost:8080/person" `
                      -Method POST `
                      -Body $person `
                      -ContentType "application/json"
    Start-Sleep -Milliseconds 200
}