detail
===

select * from user where 1=1
@if(!isEmpty(age)){
	and age = #age#
@}
@if(!isEmpty(name)){
	and name = #name#
@}


detail$count
===

select count(*) from user where 1=1
@if(!isEmpty(age)){
	and age = #age#
@}
@if(!isEmpty(name)){
	and name = #name#
@}