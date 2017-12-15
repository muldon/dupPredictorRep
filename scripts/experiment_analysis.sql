--select *
--select e.ttweight, e.bbweight,e.topictopicweight, e.tagtagweight, r.recallrate_20, r.recallrate_10, r.recallrate_5
--select e.ttweight, e.bbweight,e.topictopicweight, e.tagtagweight, to_char(r.recallrate_20/100, '0.0000') as r20, to_char(r.recallrate_10/100, '0.0000') as r10, to_char(r.recallrate_5/100, '0.0000') as r5
--select e.id, e.tag, e.maxresultsize, e.lote,e.observacao, e.bm25k, e.bm25b ,r.origem, r.recallrate_100, r.recallrate_50, r.recallrate_20, r.recallrate_10, r.recallrate_5, r.recallrate_1

select e.id, e.tag,e.lote,e.observacao, r.origem, r.recallrate_20, r.recallrate_10, r.recallrate_5
--select e.id, e.numberoftestedquestions,e.ttweight, e.bbweight,e.topictopicweight, e.tagtagweight, e.maxresultsize, e.lote,e.observacao, e.bm25k, e.bm25b ,r.origem, r.recallrate_100, r.recallrate_50, r.recallrate_20, r.recallrate_10, r.recallrate_5, r.recallrate_1
from experiment e, recallrate r
where e.id = r.experiment_id
--and e.estimateweights=true
--and e.estimateweights=false
--and e.lote = 10 
--and e.lote = 17 
--and e.lote = 20
--and e.lote = 11 
--and e.lote >= 10
--and (e.lote = 10 or e.lote = 11)
--and e.lote = 200
--and e.base like '%2017%'
--and origem like '%BM25%'
--and origem like '%Dupe%'
and origem like '%Weka%'
--and observacao like '%2016%'
and bm25k = 0.05
--and ttweight = 1 and ccweight= 1 
--and trm = 'BM25'
and maxresultsize = 10000
--and e.id > 181
and tag='java'
and app = 'Dupe'
--order by recallrate_20 desc, recallrate_10 desc
order by e.lote, origem 

--delete from experiment where id in (180,181)
--update experiment set numberoftestedquestions = 1680 where numberoftestedquestions = 1753
--select * from experiment order by lote desc;
--update experiment set app = 'DupPredictor' where id = 13146
--select * from composerweight 

--delete from composerweight 

--update experiment set app='DupPredictor' where id = 1
--SELECT to_char(3.1415927/100, 'FM0.000');

--SELECT nextval('"recallrate_id"');
--update experiment set observacao = 'Experimento simulando original - sem topicos- tratando masters nao existentes no data set' where id= 116

select e.id, e.tag, e.observacao, e.lote,r.recallrate_20,r.recallrate_10,r.recallrate_5
from experiment e, recallrate r
where e.id = r.experiment_id
and e.estimateweights=false
and app = 'DupPredictor'
AND (e.lote=102)
--AND (e.lote=101 or e.lote=102)
--order by recallrate_20 desc, recallrate_10 desc
order by e.lote desc,e.id desc, origem 

--delete from experiment where lote=101 and numberoftestedquestions=0



select e.id,e.tag, e.numberoftestedquestions, e.observacao, e.lote,r.recallrate_20,r.recallrate_10,r.recallrate_5
from experiment e, recallrate r
where e.id = r.experiment_id
--and e.estimateweights=true
and app = 'DupPredictor'
--AND (e.lote=100)
and tag like '%java%'
--AND (e.lote=100 or e.lote=101 or e.lote=102)
--order by recallrate_20 desc, recallrate_10 desc
order by e.lote,e.id desc, origem 
