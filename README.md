# Snauda — vienkāršais variants

Pēc dienas limita (piem. 4 h) visas aplikācijas "guļ" līdz rītam.
Zvani, SMS, iestatījumi un sākuma ekrāns strādā vienmēr.
Kontus noņemt nevajag, dators nav vajadzīgs, serveris nav vajadzīgs.

## Uzstādīšana
1. Android Studio → atver mapi `Snauda` → Build → Build APK.
2. Pārsūti APK uz telefonu un instalē.
3. Atver **Snauda** → uzstādi paroli.
4. Spied **"Ieslēgt / pārbaudīt servisu"** → Snauda → Ieslēgt.

**Ja Android 13+ neļauj ieslēgt** ("Restricted setting"):
Iestatījumi → Aplikācijas → Snauda → ⋮ (augšā labajā stūrī) → **Atļaut ierobežotos iestatījumus**.
Tad mēģini 4. soli vēlreiz.

## Lietošana
- **Iestatījumi 🔒** (ar paroli): limits minūtēs, plkst. kad sākas jauna diena,
  aplikācijas, kuras nekad neguļ (piem. kartes, banka).
- Kad limits beidzies, atveras ekrāns ar paroles lauku: **atbloķēt uz 15 vai 60 min**.

## Ja kaut kas nestrādā vai aizmirsi paroli
Vienkārši atinstalē: Iestatījumi → Aplikācijas → Snauda → Atinstalēt.
Iestatījumi vienmēr ir pieejami, tāpēc tas strādā arī tad, kad aplikācijas guļ.

## Kā ielikt GitHub un dabūt APK (bez Android Studio)
Vajag **datoru** (no telefona mapes augšupielādēt nevar).
1. Atpako `Snauda.zip` datorā.
2. GitHub → **New repository** → nosaukums `Snauda` → Create.
3. Spied **"uploading an existing file"**.
4. Atver atpakoto mapi `Snauda`, iezīmē **visu, kas ir tās iekšā** (Ctrl+A), un ievelc pārlūkā.
   Svarīgi: jāievelk saturs, nevis pati mape `Snauda` un nevis ZIP.
5. Lejā spied **Commit changes**.
6. Atver **Actions** → pagaidi ~3 min līdz zaļajam ✅ → uzspied uz būvējuma → lejā **Snauda-apk** → lejupielādē.
7. ZIP iekšā ir `app-debug.apk` — pārsūti uz telefonu un instalē.
