# DupPredictorRep
A replication package of DupPredictor original [work] 

### Prerequisites

Note: all the experiments were conducted over a server equipped with 80 GB RAM, 2.4 GHz on twelve cores and 64-bit Linux Mint Cinnamon operating system. We strongly recommend a similar or better hardware environment. Operation System however can be changed. 

Softwares:
1. [Java 1.8] 
2. [Mallet]
3. [Postgres 9.3]
4. [PgAdmin] (we used PgAdmin 3) but feel free to use any DB tool for PostgreSQL. 


### Installing the app.
1. Download the SO Dump of March 2017. [here] we provide two dumps. Both contains the main tables we use. They differs only in **posts** table. In one of them the table contains the original content. The other is stemmed and had the stop words removed. The next steps are described considered the fastest way to reproduce DupPredictor, in other words, the stemmed/stopped dump. If you desire to simulate the entire process, including the stemming and stop words removal, follow the instructions available in [here].
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


## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc

[work]: https://soarsmu.github.io/papers/jcst-duplicateqns.pdf
[Java 1.8]: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[Mallet]: http://mallet.cs.umass.edu/
[Postgres 9.3]: https://www.postgresql.org/download/
[PgAdmin]: https://www.pgadmin.org/download/
[here]: http://lapes.ufu.br/so/
[here]: http://lapes.ufu.br/so/2222
