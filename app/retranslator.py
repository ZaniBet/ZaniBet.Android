import sys
import os
from xml.etree import ElementTree
from requests.utils import quote
import requests
import json
import io
import re

supportedLocales = ['ru', 'es', 'de', 'zh', 'cz', 'nl',
                    'it', 'ja', 'ko', 'pl', 'ar', 'bg', 'ca',
                    'hr', 'da', 'fi', 'el', 'iw', 'hi', 'hu',
                    'in', 'lv', 'lt', 'nb', 'ro', 'sr', 'sk',
                    'sl', 'sv', 'tl', 'th', 'tr', 'vi']


def saveToFile(resdir, locale, xml):
    localizedValueDir = resdir + "/values-" + locale
    if not os.path.isdir(localizedValueDir):
        os.mkdir(localizedValueDir)

    localizedValuesFile = localizedValueDir + "/strings.xml"

    io.open(localizedValuesFile, "w", encoding='utf8').writelines(xml)
    print("Successful file creation for {} locale".format(locale))

    pass


def translateString(query, targetLocale):
    print(query)
    shieldSymbols = re.findall('[%][bBhHsScCdoxXeEfgGaA]', query)
    queries = re.split('[%][bBhHsScCdoxXeEfgGaA]', query)

    translation = ""
    for query in queries:
        preSpace = query[0] == ' '
        endspace = query[len(query)-1] == ' '
        request = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl={}&dt=t&q={}" \
        .format(targetLocale, quote(query))
        if preSpace:
            translation += " "

        result = json.loads(requests.get(request).content)[0][0][0]

        if query[0].isupper():
            result = result[0].upper() + result[1:]

        translation += result

        if endspace:
            translation = translation + " "

        if len(shieldSymbols) > 0:
            translation += shieldSymbols.pop()

    return translation
    pass


def translateTo(baseXML, locale):
    root = baseXML.getroot()

    for string in root.findall('string'):
        string.text = translateString(string.text, locale)

    for stringArray in root.findall('string-array'):
        for item in stringArray.findall('item'):
            item.text = translateString(item.text, locale)

    for stringArray in root.findall('plurals'):
        for item in stringArray.findall('item'):
            item.text = translateString(item.text, locale)
    return ElementTree.tostring(baseXML.getroot(), encoding='utf8').decode()


def main(argv):
    print("Starting script...")

    projectDir = os.getcwd()

    print("Project dir is: " + projectDir)

    print("Try to find resources directory")

    resDir = projectDir + "/app/src/main/res"

    if os.path.isdir(resDir):
        print("Successful detection: " + resDir)
    else:
        while not os.path.isdir(resDir):
            print("Input res directory manually")
            resDir = input()

    templateStringXmlLocation = resDir + "/values/strings.xml"
    if os.path.exists(templateStringXmlLocation):
        print("Template string.xml detected")
    else:
        print("Could't find string.xml in default value directory")
        pass

    for locale in supportedLocales:
        translatedXML = translateTo(ElementTree.parse(templateStringXmlLocation), locale)
        saveToFile(resDir, locale, translatedXML)
    pass


if __name__ == "__main__":
    main(sys.argv)
