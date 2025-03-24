docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi $(docker images -a -q)
#docker volume prune
docker login docker.io -u  hosseindockerhub -p 551360Azadi#
docker pull hosseindockerhub/bwwd:latest
docker pull hosseindockerhub/bwwd_db:latest
docker pull hosseindockerhub/bwwd_webassets:latest
docker pull hosseindockerhub/bwwd_nginx:latest
docker compose up





