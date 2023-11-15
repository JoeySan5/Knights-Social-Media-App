mvn clean
mvn javadoc:javadoc
mkdir -p ../docs/admin-apidocs/site
cp -r ./target/site ../docs/admin-apidocs/site