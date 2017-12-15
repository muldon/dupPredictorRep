--getQuestionsIdsByFilters
select count(id) from posts  p  
WHERE   p.posttypeId = 1 
and tags like '%ruby%'

--getQuestionsByFilters original
select * from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2011-10-01'   
order by p.creationdate 


--findClosedDuplicatedNonMasters
select * from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
order by p.creationdate 
--limit 1528

--preprocess.getDuplicatedQuestions
select * from posts  p  
WHERE   p.posttypeId = 1 
and p.id in 
( 
select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3

union 
select distinct(pl.relatedpostid)
from postlinks pl where pl.linktypeid = 3 ) 
--494956
limit 100



select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3
--429887
union 
select distinct(pl.relatedpostid)
from postlinks pl where pl.linktypeid = 3
--218957



select *  from 
posts p 
where p.posttypeid = 1
--and tags like '%html%'
--and creationdate < '2014-08-01'
--2014: 397376
--2017: 384069

order by creationdate desc
limit 100



select * from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2011-10-01'   
order by p.creationdate 
limit 1541


select * from posts  p  
WHERE   p.posttypeId = 1 
and tags like '%ruby%'
limit 100



select * from posts  p  
WHERE   p.posttypeId = 1 
--and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--order by p.creationdate 
and p.id = 59


--delete from topicvector 
select * from topicvector where postid = 73713
limit 100



select * from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id = 17364127



select *
from postlinks pl 
where pl.linktypeid = 3
and pl.postid in 
(
select p.id from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01' 
)
limit 5



--número de questões antes de 2011, data original
select count(id) from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2017-10-01'   
--2011 - 1993483
--2012 - 3531422
--2013 - 5509318
--2014 - 7712606 
--2015 - 9900984
--2016 - 12340129
--2017 - 13472796






--findClosedDuplicatedNonMasters
select count(id) from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2017-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--2011 - 22864 
--2012 - 44510
--2013 - 86861
--2014 - 134261 
--2015 - 183448
--2016 - 256706
--2017 - 293989



--verificando se todas as repetidas estao nesse espaco amostral



