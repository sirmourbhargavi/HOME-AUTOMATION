import requests

def get_all():
    get_url = "https://lights-2cc8c.firebaseio.com/.json"
    response = requests.get(get_url)
    print("FETCHING PREVIOUS VALUE : ", response.text)
    return response.json()


def update(light, status):
    print("LIGHT : ", light)
    print("STATUS : ", status)
    update_url = 'https://lights-2cc8c.firebaseio.com/.json'
    previous_response = get_all()
    previous_response[light] = status
    data = previous_response
    print("DATA : ", data)
    response = requests.put(update_url, json=data)
    print("UPDATED VALUE : ", response.text)
