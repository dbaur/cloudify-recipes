pwd=$(pwd)
mkdir -p $HOME/.cloudify/ghost
cd $HOME/.cloudify/ghost
wget https://dl.dropboxusercontent.com/u/3391962/ghost-0.4.2.zip
unzip ghost-0.4.2.zip
rm ghost-0.4.2.zip
cd $pwd
cp config.example.js $HOME/.cloudify/ghost/
cd $HOME/.cloudify/ghost
cp config.example.js config.js
sed -i "s/SERVERIP/$1/g" config.js
sed -i "s/DBIP/$2/g" config.js
npm install --production
sudo curl https://raw.githubusercontent.com/TryGhost/Ghost-Config/master/init.d/ghost -o /etc/init.d/ghost
sudo chmod 755 /etc/init.d/ghost
sudo sed -i "s,GHOST_ROOT=/var/www/ghost,GHOST_ROOT=$HOME/.cloudify/ghost,g" /etc/init.d/ghost
sudo useradd -r ghost -U
sudo chown -R ghost:ghost $HOME/.cloudify/ghost
sudo update-rc.d ghost defaults
sudo update-rc.d ghost enable
