{
    "src_folders": [
        "test"
    ],
    "output_folder": "reports",
    "custom_commands_path": "",
    "custom_assertions_path": "",
    "page_objects_path": "",
    "globals_path": "",
    "selenium": {
        "start_process": false,
        "server_path": "./node_modules/selenium-standalone/.selenium/selenium-server/3.12.0-server.jar",
        "log_path": "",
        "port": 4444,
        "cli_args": {
            "webdriver.chrome.driver": "./node_modules/selenium-standalone/.selenium/chromedriver/2.40-x64-chromedriver",
            "webdriver.gecko.driver": "./node_modules/selenium-standalone/.selenium/chromedriver/2.40-x64-chromedriver",
            "_webdriver.edge.driver": ""
        }
    },
    "test_settings": {
        "default": {
            "launch_url": "http://localhost",
            "selenium_port": 4444,
            "selenium_host": "localhost",
            "silent": false,
            "screenshots": {
                "enabled": false,
                "path": ""
            },
            "desiredCapabilities": {
                "browserName": "chrome",
                "chromeOptions": {
                    "_args": [
                        "--no-sandbox"
                    ]
                },
                "acceptSslCerts": true
            }
        },
        "chrome": {
            "desiredCapabilities": {
                "browserName": "chrome"
            }
        }
    }
}