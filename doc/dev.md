# maven deploy command
mvn -Darguments="-DskipTests=true -Dlogin.ent=xxxxxx" release:prepare
mvn -Darguments="-DskipTests=true -Dlogin.ent=xxxxxx" release:perform