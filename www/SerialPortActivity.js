var exec = require('cordova/exec');
var SerialPortActivity = function() {};

SerialPortActivity.prototype.getSerialPort = function(success, error) {
	exec(success, error, "SerialPortActivity", "getSerialPort", []);
};

SerialPortActivity.prototype.openSerialPort = function(port, baudrate, success, error) {
	exec(success, error, "SerialPortActivity", "openSerialPort", [port, baudrate]);
};

SerialPortActivity.prototype.closeSerialPort = function(success, error) {
	exec(success, error, "SerialPortActivity", "closeSerialPort", []);
};

SerialPortActivity.prototype.emission = function(byteArr, success, error) {
	exec(success, error, "SerialPortActivity", "emission", [byteArr]);
};

SerialPortActivity.prototype.portDetect = function(success, error) {
	exec(success, error, "SerialPortActivity", "portDetect", []);
};
SerialPortActivity.prototype.startNotify=function(success,error){
	exec(success, error, "SerialPortActivity", "registry", []);
}

if (!window.plugins) {
	window.plugins = {};
}

if (!window.plugins.SerialPortActivity) {
	window.plugins.SerialPortActivity = new SerialPortActivity();
}
module.exports = new SerialPortActivity();