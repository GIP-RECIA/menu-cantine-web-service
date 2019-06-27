# maven deploy command
mvn -Darguments="-DskipTests=true -Dlogin.ent=xxxxxx" release:prepare -Dlogin.ent=xxxxxx
mvn -Darguments="-DskipTests=true -Dlogin.ent=xxxxxx" release:perform -Dlogin.ent=xxxxxx