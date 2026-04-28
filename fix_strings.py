import os
import re

def fix_placeholders(match):
    tag_name = match.group(1)
    content = match.group(2)
    
    # Find all placeholders like %d, %s, %f
    # But skip if they are already indexed like %1$d
    placeholders = re.findall(r'%(?!\d+\$)[dsf]', content)
    
    if len(placeholders) > 1:
        # Replace each non-indexed placeholder with an indexed one
        count = 1
        new_content = content
        while True:
            # Match the first non-indexed placeholder
            m = re.search(r'%(?!\d+\$)([dsf])', new_content)
            if not m:
                break
            # Replace it with %N$type
            new_content = new_content[:m.start()] + f"%{count}${m.group(1)}" + new_content[m.end():]
            count += 1
        return f'<string name="{tag_name}">{new_content}</string>'
    return match.group(0)

def process_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Match <string name="tag_name">content</string>
    pattern = r'<string name="([^"]+)">([^<]*)</string>'
    new_content = re.sub(pattern, fix_placeholders, content)
    
    if content != new_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed: {file_path}")

def main():
    root_dir = r"c:\Users\Hama9\Desktop\streamflix-main\app\src\main\res"
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if file == "strings.xml":
                process_file(os.path.join(root, file))

if __name__ == "__main__":
    main()
