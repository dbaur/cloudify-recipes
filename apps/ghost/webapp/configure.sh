cd $HOME/.cloudify/ghost
sudo sed -i "s/DBIP/$2/g" config.js
sudo sed -i "s/SERVERIP/$1/g" config.js
