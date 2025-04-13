#docker login docker.io -u  hosseindockerhub -p 551360Azadi#
#echo are you updating BestWorldWideDeals Image? [y/n]
#read varname
#if [ "$varname" = "y" ]
#then
#	docker build  -t  hosseindockerhub/bwwd .
#	docker push hosseindockerhub/bwwd
#	cd ../
#fi
#echo are you updating DB Image? [y/n]
#read varname
#if [ "$varname" = "y" ]
#then
#	cd BestWorldWideDeals/src/main/db
#	docker build  -t  hosseindockerhub/bwwd_db .
#	docker push hosseindockerhub/bwwd_db
#	cd ../../../../
#fi
#echo are you updating NGINX Image? [y/n]
#read varname
#if [ "$varname" = "y" ]
#then
#	cd BestWorldWideDeals/src/main/nginx
#	docker build  -t  hosseindockerhub/bwwd_nginx .
#	docker push hosseindockerhub/bwwd_nginx
#	cd ../../../../
#fi
#
#echo are you updating BestWorldWideDeals_WebAssets Image? [y/n]
#read varname
#if [ "$varname" = "y" ]
#then
#  cd BestWorldWideDeals_WebAssets
#  docker build  -t  hosseindockerhub/bwwd_webassets .
#  docker push hosseindockerhub/bwwd_webassets
#  cd ../BestWorldWideDeals
#fi
#docker logout

docker login docker.io -u  hosseindockerhub -p 551360Azadi#
docker build  -t  hosseindockerhub/bwwd .
docker push hosseindockerhub/bwwd
cd ../
cd BestWorldWideDeals/src/main/db
docker build  -t  hosseindockerhub/bwwd_db .
docker push hosseindockerhub/bwwd_db
cd ../../../../
cd BestWorldWideDeals/src/main/nginx
docker build  -t  hosseindockerhub/bwwd_nginx .
docker push hosseindockerhub/bwwd_nginx
cd ../../../../
cd BestWorldWideDeals_WebAssets
docker build  -t  hosseindockerhub/bwwd_webassets .
docker push hosseindockerhub/bwwd_webassets
cd ../BestWorldWideDeals
docker logout



