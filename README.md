# Picture-Encrypter
A command line tool that makes encrypting and decrypting pictures easy.

## How to Use:
`java PED <key> <command> <image path>`

| Parameter Name | Description |
| --- | --- |
| key | The key to encrypt and decrypt with. |
| command | Either `encrypt` or `decrypt`. |
| image path | The path to the image or folder for multiple images |
>Notes:
>
> * Key must be the same for both encryption and decryption to be successful
> * If specifying a folder leave `/` of the image path 
## Image Files Supported:
* png
* jpg, jpeg

## Libraries Used:
* [pngj](https://github.com/leonbloy/pngj)

Known issues:
* Compression for JPEG files are more noticeable 
