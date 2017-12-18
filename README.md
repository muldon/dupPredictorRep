# DupPredictorRep
A replication package of DupPredictor original [work] 

### Prerequisites

Note: all the experiments were conducted over a server equipped with 80 GB RAM, 2.4 GHz on twelve cores and 64-bit Linux Mint Cinnamon operating system. We strongly recommend a similar or better hardware environment. Operation System however can be changed. 

Softwares:
1. [Java 1.8] 
2. [Mallet]
3. [Postgres 9.3]
3. [PgAdmin] (we used PgAdmin 3) but feel free to use any DB tool for PostgreSQL. Configure your DB to accept local connections. An example of *pg_hba.conf* configuration:

```
...
# TYPE  DATABASE        USER            ADDRESS                 METHOD
# "local" is for Unix domain socket connections only
local   all             all                                     md5
# IPv4 local connections:
host    all             all             127.0.0.1/32            md5
...
```

5. [Maven 3](https://maven.apache.org/)

### Installing the app.
1. Download the SO [Dump of March 2017]. We provide two dumps where both contains the main tables we use. They differs only in **posts** table. In one of them the table contains the original content, while the other is stemmed and had the stop words removed. The next steps are described considered the fastest way to reproduce DupPredictor, in other words, the stemmed/stopped dump. If you desire to simulate the entire process, including the stemming and stop words removal, follow the instructions available in [preprocess] for stemming and removing the stop words, then procceed in next steps.
2. On your DB tool, create a new database named stackoverflow2017. This is a query example:
```
CREATE DATABASE stackoverflow2017
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
```
3. Restore the downloaded dump to the created database. 

Obs: restoring this dump would require at least 100 Gb of free space. If your Operation System runs in a partition with insufficient free space, create a tablespace pointing to a larger partition and associate the database to it by replacing the "TABLESPACE" value to the new tablespace name: `TABLESPACE = tablespacename`. 

4. Assert that the database is sound. Execute the following SQL command: `select title,body,tags,tagssyn,code  from posts where title is not null limit 10`. The return should list the main fields for 10 posts. 

5. Assert your Mallet instalation is sound. In a Terminal, go to your Mallet folder and execute the command: `bin/mallet --help`. This should return a list of commands. 

6. Assert Maven is correctly installed. In a Terminal enter with the command: `mvn --version`. This should return the version of Maven. 


## Setting parameters

Edit the file *application.properties* under *src/main/resources* and set the parameters bellow "##### INPUT PARAMETERS #####". The file comes with default values for simulating DupPredictor original work. You need to fill only two variables: `spring.datasource.password=YOUR_DB_PASSWORD` and `mallet.dir = YOUR_MALLET_DIR`. Change `spring.datasource.username` if your db user is not postgres. 

Obs: the file *application.properties* have 3 steps, where in each one you can set parameters. Default values for *application.properties*:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/stackoverflow2017
spring.datasource.username=postgres
spring.datasource.password=YOUR_DB_PASSWORD

mallet.dir = PATH_TO_MALLET/mallet-2.0.8

##--------------------------step 1---------------------
useLDA 	   = false
numTopics  = 100
buildMalletTopicFiles = false
runMalletCommands     = false
loadVectorsToDB       = false

##--------------------------step 2---------------------
estimateWeights = false
trainingPercentOfQuestions = 20

##--------------------------step 3---------------------
calculateRecallRates = true
observation = simulating duppredictor
closedDuplicatedNonMastersLimit = 1528
#closedDuplicatedNonMastersLimit = 
allQuestionsByFiltersLimit    =  2000000
#allQuestionsByFiltersLimit    =  
maxResultSize = 1000
#testingPercentOfQuestions = 10
testingPercentOfQuestions = 100

tagFilter = 

#yyyy-mm-dd
#The paper extracts the first 2M posts < 2011-10-01 
maxCreationDate = 2011-10-01
lote = 1
```

## Running a quick test

1. Leave the file *application.properties* with default values. In a terminal, go to the Project_folder and build the jar file with the Maven command: `mvn package -Dmaven.test.skip=true`. Assert that duppredictor.jar is built under target folder. 

2. Go to Project_folder/target and run the command to execute DupPredictorRep: `java -Xms1024M -Xmx70g -jar ./duppredictor.jar`. The Xmx value may be bigger if you change the "maxCreationDate" parameter to a more recent date. 

Obs: the complete test where LDA is enabled take too long, so the default parameters have LDA disabled. If you want to perform a full test, go to the the next section. 


## Running a complete test (optinal)

1. Edit the file *application.properties*. 

There are 2 possibilities: 

a) Estimating weights: set the variables `estimateWeights = true`, `calculateRecallRates = false`. Leave the others in their default values. This will simulate the estimation of weights phase of the paper. See the results for the weights. 

b) Running the app with topics enabled: 

First, enable topics by setting `useLDA = true`, `buildMalletTopicFiles = true`, `calculateRecallRates = false`. Leave the other variables in their default values. Run the app (see **Running a quick test**). After this, assert that in folder *mallet.dir/topics* contains almost 2 milion text files.

Second, run mallet commands (this is a lot faster than running through the app): in a terminal, go to your *mallet.dir* folder and execute `bin/mallet import-dir --input topics --output topic.mallet --keep-sequence`. Use the generated file *topic.mallet* to train topics by executing the command: `bin/mallet train-topics --input topic.mallet --num-topics 100 --output-state topic-state.gz --output-topic-keys topics_keys.txt --output-doc-topics topics_duppredictor.txt`. 

Third, set variables: `useLDA = true`, `buildMalletTopicFiles = false`, `loadVectorsToDB = true`, `calculateRecallRates = true`. Leave the other variables in their default values. Run the app (see **Running a quick test**). 

### Results

The results are displayed in the terminal but also stored in the database in tables **experiment** and **recallrate** . The following query should return the results:  
`select * from experiment e, recallrate r where e.id = r.experiment_id order by e.lote,e.id desc, origem `


## Authors

* Rodrigo Fernandes  - *Initial work* - [Muldon](https://github.com/muldon)
* Klerisson Paixao - [Klerisson](http://klerisson.github.io/)
* Marcelo Maia - [Marcelo](http://buscatextual.cnpq.br/buscatextual/visualizacv.do?id=K4791753E8)


## License
???
//This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details



[work]: https://soarsmu.github.io/papers/jcst-duplicateqns.pdf
[Java 1.8]: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[Mallet]: http://mallet.cs.umass.edu/
[Postgres 9.3]: https://www.postgresql.org/download/
[PgAdmin]: https://www.pgadmin.org/download/
[Dump of March 2017]: http://lapes.ufu.br/so/
[preprocess]: https://github.com/muldon/preprocessor
