import re

with open('/Users/mudasirmushtaq/Documents/app/northend/backend/whatsapp_inbox.py', 'r') as f:
    code = f.read()

direct_endpoint = """
    class SendDirectIn(BaseModel):
        phone: str
        template_name: str
        template_language: str
        components: list = []

    @router.post("/whatsapp/send-direct")
    async def send_direct(req: SendDirectIn, _user=Depends(require_super_admin_dep)):
        token = _cfg("WHATSAPP_ACCESS_TOKEN")
        phone_id = _cfg("WHATSAPP_PHONE_NUMBER_ID")
        if not token or not phone_id:
            raise HTTPException(500, "WhatsApp credentials not configured")
            
        clean_phone = "".join(filter(str.isdigit, req.phone))
        payload = {
            "messaging_product": "whatsapp",
            "to": clean_phone,
            "type": "template",
            "template": {
                "name": req.template_name,
                "language": {"code": req.template_language},
                "components": req.components
            }
        }
        
        url = f"https://graph.facebook.com/{VERSION}/{phone_id}/messages"
        async with httpx.AsyncClient(timeout=10) as client:
            resp = await client.post(url, headers={"Authorization": f"Bearer {token}"}, json=payload)
            
        if resp.status_code >= 400:
            try:
                err = resp.json()
            except Exception:
                err = {"raw": resp.text}
            raise HTTPException(resp.status_code, err)
            
        # Ensure a thread exists for this contact so they appear in the inbox
        contact = await _upsert_contact(clean_phone, "Unknown")
        thread = await _upsert_thread(contact, f"Started chat with template: {req.template_name}", "outbound", now_iso())
        
        return {"status": "success", "thread": thread}

"""

if "class SendDirectIn" not in code:
    code = code.replace('async def list_templates', direct_endpoint + '\n    async def list_templates')
    with open('/Users/mudasirmushtaq/Documents/app/northend/backend/whatsapp_inbox.py', 'w') as f:
        f.write(code)
    print("Backend API patched with send-direct endpoint.")
else:
    print("Backend already has send-direct endpoint.")
