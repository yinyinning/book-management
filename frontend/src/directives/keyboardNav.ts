import type { Directive } from 'vue'

const handleMap = new WeakMap<HTMLElement, (e: KeyboardEvent) => void>()

export const keyboardNav :Directive = {
    mounted(el, binding) {
        const handleKeydown = (e: KeyboardEvent) => {
            const target = e.target as HTMLElement
            if (target.tagName !== 'INPUT' && target.tagName !== 'TEXTAREA') {
                return
            }
            const inputs = Array.from(el.querySelectorAll('input:not([disabled]), textarea:not([disabled])')) as HTMLElement[]
            if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
                e.preventDefault()
                const nextIndex =e.key === 'ArrowDown' ? 1 : -1    
                inputs[inputs.indexOf(target) + nextIndex]?.focus()
            }else if (e.key === 'Enter') {
                e.preventDefault()
                binding.value?.()
            }
        }
        el.addEventListener('keydown', handleKeydown)
        handleMap.set(el, handleKeydown)
    },
    unmounted(el) {
        const handle = handleMap.get(el)
        if (handle) {
            el.removeEventListener('keydown', handle)
            handleMap.delete(el)
        }
    }
}